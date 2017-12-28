import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class Program {

	protected Shell shlDockerfilesToRdf;

	protected ArrayList<File> dockerFiles = new ArrayList<File>();
	protected ArrayList<File> dockerFilesToProcess = new ArrayList<File>();
	protected ArrayList<File> rdfFiles = new ArrayList<File>();
	
	protected ListViewer dockerFilesListViewer;
	protected List dockerFilesList;
	
	protected ListViewer RDFlistViewer;
	protected List RDFlist;

	
	private Text txtWelcomeToSwiprolog;
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		
		try {
			Program window = new Program();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void createDockerFiles()
	{
		this.dockerFiles.add(new File("E:\\DockerFilesToRDF\\DockerFilesToRDF\\sources\\dockeronto"));
		this.dockerFiles.add(new File("E:\\DockerFilesToRDF\\DockerFilesToRDF\\sources\\django"));
		this.dockerFiles.add(new File("E:\\DockerFilesToRDF\\DockerFilesToRDF\\sources\\python"));
		this.dockerFiles.add(new File("E:\\DockerFilesToRDF\\DockerFilesToRDF\\sources\\rails"));
	}
	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		
		this.createDockerFiles();
		
		createContents();
		
		shlDockerfilesToRdf.open();
		shlDockerfilesToRdf.layout();
		
		while (!shlDockerfilesToRdf.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
	    
	    
		shlDockerfilesToRdf = new Shell();
		shlDockerfilesToRdf.setSize(800, 400);
		shlDockerfilesToRdf.setText("Dockerfiles to RDF 0.17.12.28");
		shlDockerfilesToRdf.setLayout(new GridLayout(2, false));
		
		Menu menu = new Menu(shlDockerfilesToRdf, SWT.BAR);
		shlDockerfilesToRdf.setMenuBar(menu);
		
		MenuItem mntmDockerfiles = new MenuItem(menu, SWT.CASCADE);
		mntmDockerfiles.setText("Dockerfiles");
		
		Menu menu_1 = new Menu(mntmDockerfiles);
		mntmDockerfiles.setMenu(menu_1);
		
		MenuItem mntmLoad = new MenuItem(menu_1, SWT.NONE);
		mntmLoad.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
					
					FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN | SWT.MULTI);
    			    dialog.setText("Select the files in DOCKER fomat to upload");
    			    // TODO: Debug purposes only
    			    dialog.setFilterPath("E:\\DockerFilesToRDF\\DockerFilesToRDF\\sources\\");
    			    String result = dialog.open();
    			    
    			    if (result != null)
    			    {
    			    	// TODO: add to existing positions
//    			    	dockerFiles.clear();
//    			    	dockerFilesList.removeAll();
    			    	
        			    for (String dockerFile : dialog.getFileNames())
    					{
    				    	dockerFiles.add(new File(dialog.getFilterPath() + File.separator + dockerFile));
    				    	dockerFilesList.add(dockerFile);  
    					}
    			    } else {
    			    	// todo: cancelled
    			    }
			}
		});
		mntmLoad.setText("Load\tCtrl+O");
		mntmLoad.setAccelerator(SWT.MOD1 + 'O');
		
		MenuItem mntmNewItem = new MenuItem(menu_1, SWT.NONE);
		mntmNewItem.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{		       
			        if(dockerFilesList.getSelectionCount() == 0)
			        {
						MessageBox dialog = new MessageBox(shlDockerfilesToRdf, SWT.ICON_ERROR | SWT.OK);
						dialog.setText("Error");
						dialog.setMessage("Please choose at least one DOCKER file to process!");
						dialog.open();
			          return;
			        }
			        
			        dockerFilesToProcess.clear();
			        RDFlist.removeAll();
			        
					for (int i = 0; i < dockerFilesList.getSelectionIndices().length; i++)
					{
						String newFile = dockerFilesList.getItem(dockerFilesList.getSelectionIndices()[i]).replaceAll("\\\\sources\\\\", "\\\\rdf\\\\");
						dockerFilesToProcess.add(new File(newFile));
					}
					
					// TODO: remove duplicates
					dockerFilesToProcess.forEach(file -> RDFlist.add(file.getAbsolutePath()));
					
			}
		});
		mntmNewItem.setText("Process\tCtrl+P");
		mntmNewItem.setAccelerator(SWT.MOD1 + 'P');

		MenuItem mntmSeparatordockerfiles = new MenuItem(menu_1, SWT.SEPARATOR);
		mntmSeparatordockerfiles.setText("separator-dockerfiles");
		
		MenuItem mntmClear = new MenuItem(menu_1, SWT.NONE);
		mntmClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
		    	dockerFiles.clear();
		    	dockerFilesList.removeAll();
				
		        dockerFilesToProcess.clear();
		        RDFlist.removeAll();
			}
		});
		mntmClear.setText("Clear");
		
		MenuItem mntmRdf = new MenuItem(menu, SWT.CASCADE);
		mntmRdf.setText("RDF");
		
		Menu menu_2 = new Menu(mntmRdf);
		mntmRdf.setMenu(menu_2);
		
		MenuItem mntmNewItem_1 = new MenuItem(menu_2, SWT.NONE);
		mntmNewItem_1.setText("Insert to db");
		
		MenuItem mntmProlog = new MenuItem(menu, SWT.CASCADE);
		mntmProlog.setText("Prolog");
		
		Menu menu_3 = new Menu(mntmProlog);
		mntmProlog.setMenu(menu_3);
		
		MenuItem mntmVersion = new MenuItem(menu_3, SWT.NONE);
		mntmVersion.addSelectionListener(
		new SelectionAdapter() 
			{
				@Override
				public void widgetSelected(SelectionEvent se) {
					// create a dialog with ok and cancel buttons and a question icon
					MessageBox dialog = new MessageBox(shlDockerfilesToRdf, SWT.ICON_INFORMATION | SWT.OK);
					dialog.setText("Prolog version");
					
					
					try {
						Process P = new ProcessBuilder("E:\\Program Files\\swipl\\bin\\swipl.exe", "--version").start();
						BufferedReader processOutput = new BufferedReader(new InputStreamReader(P.getInputStream()));
	//					BufferedWriter processInput = new BufferedWriter(new OutputStreamWriter(P.getOutputStream()));
						
					    String line;
					    StringBuilder outputMessage = new StringBuilder("");
					    while ((line = processOutput.readLine()) != null)
					    {
					    	outputMessage.append(line).append("\n");   
					    }
					    dialog.setMessage(outputMessage.toString());
					    
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						dialog.setMessage(e.getMessage());
					}

					dialog.open();
	
				}
			}
		);
		mntmVersion.setText("Version");
		
		MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
		mntmHelp.setText("Help");
		
		Menu menu_4 = new Menu(mntmHelp);
		mntmHelp.setMenu(menu_4);
		
		Group grpDockerfiles = new Group(shlDockerfilesToRdf, SWT.NONE);
		GridData gd_grpDockerfiles = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_grpDockerfiles.heightHint = 104;
		gd_grpDockerfiles.widthHint = 381;
		grpDockerfiles.setLayoutData(gd_grpDockerfiles);
		grpDockerfiles.setText("DOCKER files");
		
		dockerFilesListViewer = new ListViewer(grpDockerfiles, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		dockerFilesList = dockerFilesListViewer.getList();
		dockerFilesList.setItems(new String[] {});
		
	    // TODO: Debug purposes only
		dockerFiles.forEach(file -> dockerFilesList.add(file.getAbsolutePath()));
		
		dockerFilesList.setBounds(10, 22, 367, 90);

		Group grpRdf = new Group(shlDockerfilesToRdf, SWT.NONE);
		GridData gd_grpRdf = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_grpRdf.heightHint = 105;
		gd_grpRdf.widthHint = 375;
		grpRdf.setLayoutData(gd_grpRdf);
		grpRdf.setText("RDF files");
		
		RDFlistViewer = new ListViewer(grpRdf, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		RDFlist = RDFlistViewer.getList();
		RDFlist.setEnabled(false);
		RDFlist.setBounds(10, 20, 361, 93);
		
		Group grpProlog = new Group(shlDockerfilesToRdf, SWT.NONE);
		GridData gd_grpProlog = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_grpProlog.widthHint = 767;
		grpProlog.setLayoutData(gd_grpProlog);
		grpProlog.setText("Prolog output:");
		
		txtWelcomeToSwiprolog = new Text(grpProlog, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		txtWelcomeToSwiprolog.setText("Welcome to SWI-Prolog (threaded, 64 bits, version 7.6.3)\r\nSWI-Prolog comes with ABSOLUTELY NO WARRANTY. This is free software.\r\nPlease run ?- license. for legal details.\r\n\r\nFor online help and background, visit http://www.swi-prolog.org\r\nFor built-in help, use ?- help(Topic). or ?- apropos(Word).\r\n\r\n?- ");
		txtWelcomeToSwiprolog.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		txtWelcomeToSwiprolog.setEditable(false);
		txtWelcomeToSwiprolog.setBounds(10, 21, 753, 125);
		
		Group group_1 = new Group(shlDockerfilesToRdf, SWT.NONE);
		new Label(shlDockerfilesToRdf, SWT.NONE);

	}
}
