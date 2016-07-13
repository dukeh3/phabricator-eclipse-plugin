package nu.gic.test.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.phabricator.conduit.ConduitException;
import org.phabricator.conduit.raw.Conduit;
import org.phabricator.conduit.raw.ConduitFactory;
import org.phabricator.conduit.raw.ManiphestModule.TaskResult;
import org.phabricator.conduit.raw.ProjectModule.ProjectResult;
import org.phabricator.conduit.raw.ProjectModule.QueryResult;

public class IssuesTree {

	static class Avatar {
		String getText(int col) {
			return col == 0 ? toString() : "";
		}

		Image getColumnImage(int col) {
			return col == 0 ? PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT)
					: null;
		}
	}

	static class Task extends Avatar {
		List<Task> blocks = new ArrayList<>();

		TaskResult tr;

		@Override
		public String toString() {
			return tr.getTitle();
		}

	}

	static class Project extends Avatar {
		List<Task> tasks = new ArrayList<>();

		ProjectResult pr;

		@Override
		public String toString() {
			return pr.getName();
		}
	}

	Map<String, Project> projMap = new HashMap<>();
	Map<String, Task> taskMap = new HashMap<>();

	public IssuesTree() {
	}

	void init() {

		// String baseUrl = "http://ph.labs.h3.se";
		// String apiToken = "api-fgzgm3c7opso7z42auep5abfw47x";

		String baseUrl = "http://p.gic.nu";
		String apiToken = "api-kyirxoio6fn74ihksug2cazi3sqj";

		Conduit conduit = ConduitFactory.createConduit(baseUrl, apiToken);

		try {

			QueryResult qr = conduit.project.query(null, null, null, null, null, null, null, null, null, null);

			for (ProjectResult pr : qr.getData().values()) {
				Project p = new Project();
				p.pr = pr;
				projMap.put(pr.getPhid(), p);
			}

			org.phabricator.conduit.raw.ManiphestModule.QueryResult qr2 = conduit.maniphest.query(null, null, null,
					null, null, null, null, null, null, null, null);

			for (TaskResult tr : qr2.values()) {

				Task t = new Task();
				t.tr = tr;

				for (String projPhid : tr.getProjectPHIDs()) {
					Project p = projMap.get(projPhid);

					if (p != null)
						p.tasks.add(t);
				}

				taskMap.put(tr.getPhid(), t);
			}

			for (Task t : taskMap.values()) {
				for (String taskPhid : t.tr.getDependsOnTaskPHIDs()) {
					taskMap.get(taskPhid).blocks.add(t);
				}
			}

		} catch (ConduitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

		IssuesTree it = new IssuesTree();
		it.init();

		for (Project p : it.projMap.values()) {

			System.out.println(p.pr.getName());

			for (Task t : p.tasks) {
				if (t.blocks.isEmpty()) {
					System.out.println(t.tr.getPhid());
					System.out.println(t.tr.getObjectName());
					System.out.println(t.tr.getDescription());
				}
			}
		}

	}

}
