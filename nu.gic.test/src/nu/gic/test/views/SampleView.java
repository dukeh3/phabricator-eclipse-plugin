package nu.gic.test.views;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.*;
import org.phabricator.conduit.ConduitException;
import org.phabricator.conduit.raw.Conduit;
import org.phabricator.conduit.raw.ConduitFactory;
import org.phabricator.conduit.raw.UserModule.UserResult;

import nu.gic.test.views.IssuesTree.Project;
import nu.gic.test.views.IssuesTree.Task;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.SWT;
import org.eclipse.core.runtime.IAdaptable;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class SampleView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "nu.gic.test.views.SampleView";

	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	// class TreeObject implements IAdaptable {
	// private String name;
	// private TreeParent parent;
	//
	// public TreeObject(String name) {
	// this.name = name;
	// }
	//
	// public String getName() {
	// return name;
	// }
	//
	// public void setParent(TreeParent parent) {
	// this.parent = parent;
	// }
	//
	// public TreeParent getParent() {
	// return parent;
	// }
	//
	// public String toString() {
	// return getName();
	// }
	//
	// public <T> T getAdapter(Class<T> key) {
	// return null;
	// }
	// }

	// class TreeParent extends TreeObject {
	// private ArrayList children;
	//
	// public TreeParent(String name) {
	// super(name);
	// children = new ArrayList();
	// }
	//
	// public void addChild(TreeObject child) {
	// children.add(child);
	// child.setParent(this);
	// }
	//
	// public void removeChild(TreeObject child) {
	// children.remove(child);
	// child.setParent(null);
	// }
	//
	// public TreeObject[] getChildren() {
	// return (TreeObject[]) children.toArray(new TreeObject[children.size()]);
	// }
	//
	// public boolean hasChildren() {
	// return children.size() > 0;
	// }
	// }

	class ViewContentProvider implements ITreeContentProvider {

//		IssuesTree it;

		// private TreeParent invisibleRoot;

		public Object[] getElements(Object parent) {

			System.out.println("SampleView.ViewContentProvider.getElements()" + parent);

			// if (parent.equals(getViewSite())) {
			//
			// if (it == null) {
			// it = new IssuesTree();
			// it.init();
			// }
			// //
			// // if (invisibleRoot==null) initialize();
			//
			// return it.projMap.values().toArray();
			// }

			return getChildren(parent);
		}

		public Object getParent(Object child) {

			System.out.println("SampleView.ViewContentProvider.getParent()" + child);

			if (child instanceof Project) {
				return it;
			} else if (child instanceof Task) {
				Task task = (Task) child;
				return task.blocks.isEmpty() ? it.projMap.get(task.tr.getProjectPHIDs().get(0)) : task.blocks.get(0);
			}

			return null;
		}

		public Object[] getChildren(Object parent) {

			System.out.println("SampleView.ViewContentProvider.getChildren()" + parent);

			if (parent instanceof IssuesTree) {
				return ((IssuesTree) parent).projMap.values().toArray();
			}

			if (parent instanceof Project) {

				List<Task> topList = new LinkedList<>();

				Project p = ((Project) parent);

				for (Task t : p.tasks) {
					if (t.blocks.isEmpty())
						topList.add(t);
				}

				return topList.toArray();
			}

			if (parent instanceof Task) {
				Task t = (Task) parent;

				List<Task> topList = new LinkedList<>();

				for (String phid : t.tr.getDependsOnTaskPHIDs()) {
					topList.add(it.taskMap.get(phid));
				}

				return topList.toArray();
			}

			return new Object[0];
		}

		public boolean hasChildren(Object parent) {
			return getChildren(parent).length != 0;
		}

		/*
		 * We will set up a dummy model to initialize tree heararchy. In a real
		 * code, you will connect to a real model and expose its hierarchy.
		 */
		// private void initialize() {
		//
		// it.init();
		//
		// TreeObject to1 = new TreeObject("Leaf 1");
		// TreeObject to2 = new TreeObject("Leaf 2");
		// TreeObject to3 = new TreeObject("Leaf 3");
		// TreeParent p1 = new TreeParent("Parent 1");
		// p1.addChild(to1);
		// p1.addChild(to2);
		// p1.addChild(to3);
		//
		// TreeObject to4 = new TreeObject("Leaf 4");
		// TreeParent p2 = new TreeParent("Parent 2");
		// p2.addChild(to4);
		//
		// TreeParent root = new TreeParent("Root");
		// root.addChild(p1);
		// root.addChild(p2);
		//
		// invisibleRoot = new TreeParent("");
		// invisibleRoot.addChild(root);
		// }
	}

//	class ViewLabelProvider extends LabelProvider {
//
//		public String getColumnText(Object obj, int index) {
//			System.out.println("SampleView.ViewLabelProvider.getColumnText()" + obj);
//
//			return getText(obj);
//		}
//
//		public Image getColumnImage(Object obj, int index) {
//			System.out.println("SampleView.ViewLabelProvider.getColumnImage()" + obj);
//
//			return getImage(obj);
//		}
//
//		public String getText(Object obj) {
//
//			System.out.println("SampleView.ViewLabelProvider.getText()" + obj);
//
//			return obj.toString();
//		}
//
//		public Image getImage(Object obj) {
//			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
//			// if (obj instanceof TreeParent)
//			// imageKey = ISharedImages.IMG_OBJ_FOLDER;
//			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
//		}
//	}

	/**
	 * The constructor.
	 */
	public SampleView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		it.init();

		Tree addressTree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		addressTree.setHeaderVisible(true);
		// m_treeViewer = new TreeViewer(addressTree);
		//
		//
		// m_treeViewer.setContentProvider(new AddressContentProvider());
		// m_treeViewer.setLabelProvider(new TableLabelProvider());
		// List<City> cities = new ArrayList<City>();
		// cities.add(new City());
		// m_treeViewer.setInput(cities);
		// m_treeViewer.expandAll();

//		viewer = new TreeViewer(addressTree, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer = new TreeViewer(addressTree);
		drillDownAdapter = new DrillDownAdapter(viewer);

		TreeColumn column1 = new TreeColumn(addressTree, SWT.LEFT);
		addressTree.setLinesVisible(true);
		column1.setAlignment(SWT.LEFT);
		column1.setText("Name");
		column1.setWidth(160);
		TreeColumn column2 = new TreeColumn(addressTree, SWT.RIGHT);
		column2.setAlignment(SWT.LEFT);
		column2.setText("Owner");
		column2.setWidth(100);
		TreeColumn column3 = new TreeColumn(addressTree, SWT.RIGHT);
		column3.setAlignment(SWT.LEFT);
		column3.setText("Creation");
		column3.setWidth(35);
		TreeColumn column4 = new TreeColumn(addressTree, SWT.RIGHT);
		column4.setAlignment(SWT.LEFT);
		column4.setText("Done");
		column4.setWidth(35);

		

		viewer.setContentProvider(new ViewContentProvider());

		viewer.setInput(it);
//		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setLabelProvider(new TableLabelProvider());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "nu.gic.test.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}
	
	 class TableLabelProvider implements ITableLabelProvider{
		 
			public String getColumnText(Object obj, int index) {
				System.out.println("SampleView.ViewLabelProvider.getColumnText()" + obj);

				return getText(obj);
			}

			public Image getColumnImage(Object obj, int index) {
				System.out.println("SampleView.ViewLabelProvider.getColumnImage()" + obj);

				return index == 0 ? getImage(obj) : null;
			}

			public String getText(Object obj) {

				System.out.println("SampleView.ViewLabelProvider.getText()" + obj);

				return obj.toString();
			}

			public Image getImage(Object obj) {
				String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
				// if (obj instanceof TreeParent)
				// imageKey = ISharedImages.IMG_OBJ_FOLDER;
				return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
			}
		 
		 
//	      public Image getColumnImage(Object element, int columnIndex){
//	         return null;
//	      }
//	 
//	      public String getColumnText(Object element, int columnIndex){
//	         return null;
//	      }
	 
	      public void addListener(ILabelProviderListener listener){
	      }
	 
	      public void dispose(){
	      }
	 
	      public boolean isLabelProperty(Object element, String property){
	         return false;
	      }
	 
	      public void removeListener(ILabelProviderListener listener){
	      }
	   }		
	

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				SampleView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				showMessage("Double-click detected on " + obj.toString());
			}
		};
	}

	IssuesTree it = new IssuesTree();

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();

				Control control = viewer.getControl();

				String baseUrl = "http://ph.labs.h3.se";
				String apiToken = "api-fgzgm3c7opso7z42auep5abfw47x";
				Conduit conduit = ConduitFactory.createConduit(baseUrl, apiToken);

				MessageDialog.openInformation(control.getShell(), "Bundle", "Hello, Eclipse world");

				try {
					for (UserResult ur : conduit.user.query(null, null, null, null, null, null, null)) {
						MessageDialog.openInformation(control.getShell(), "Bundle",
								"Hello, Eclipse world " + ur.getRealName());
					}
				} catch (ConduitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				MessageDialog.openInformation(control.getShell(), "Bundle", ">");

			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "Sample View", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
