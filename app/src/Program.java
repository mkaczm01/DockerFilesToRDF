import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.parser.sparql.ast.ParseException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import Dialog.AppLoadingDialog;
import SelectionListener.ConfigurationSelectionListener;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;

public class Program
{

	protected Shell shlDockerfilesToRdf;

	protected Properties appProperties;

	protected ArrayList<File> dockerFiles = new ArrayList<File>();
	protected ArrayList<File> dockerFilesToProcess = new ArrayList<File>();
	protected ListViewer dockerFilesListViewer;
	protected List dockerFilesList;

	protected ArrayList<File> rdfFiles = new ArrayList<File>();
	protected ArrayList<File> rdfFilesToProcess = new ArrayList<File>();
	protected ListViewer RDFlistViewer;
	protected List RDFlist;

	protected Button btnButtonRunQuery;

	private Text txtWelcomeToSwiprolog;
	private Text textSparql;

	protected Repository RDFDB;
	private Text textSparqlOutput;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{

		try
		{
			Program window = new Program();
			window.open();
		} catch (Exception e)
		{
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
	public void open()
	{
		Display display = Display.getDefault();

		// TODO: debug purposes only
//		createDockerFiles();

		initializeSettings();

		initializeRDFDatabase();

		createContents();

		shlDockerfilesToRdf.open();
		shlDockerfilesToRdf.layout();
		
		shlDockerfilesToRdf.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	  System.out.println("RDF database shutdown...");
		    	  RDFDB.shutDown();
		      }
		    });

		while (!shlDockerfilesToRdf.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
	}

	private void initializeRDFDatabase()
	{
		RDFDB = new SailRepository(new MemoryStore());
		RDFDB.initialize();

	}

	protected void initializeSettings()
	{

		File configFile = new File("app.settings");

		FileReader reader;
		try
		{
			reader = new FileReader(configFile);

			appProperties = new Properties();

			// load the properties file:
			appProperties.load(reader);
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents()
	{

		shlDockerfilesToRdf = new Shell();
		shlDockerfilesToRdf.setSize(800, 620);
		shlDockerfilesToRdf.setText("Dockerfiles to RDF 0.18.01.10");
		shlDockerfilesToRdf.setLayout(new GridLayout(2, false));

		Menu menu = new Menu(shlDockerfilesToRdf, SWT.BAR);
		shlDockerfilesToRdf.setMenuBar(menu);
		
		MenuItem mntmProgram = new MenuItem(menu, SWT.CASCADE);
		mntmProgram.setText("Program");
		
		Menu menu_5 = new Menu(mntmProgram);
		mntmProgram.setMenu(menu_5);
				
						MenuItem mntmClear = new MenuItem(menu_5, SWT.NONE);
						mntmClear.addSelectionListener(new SelectionAdapter()
						{
							/**
							 * CLEAR clicked
							 */
							@Override
							public void widgetSelected(SelectionEvent e)
							{
								dockerFiles.clear();
								dockerFilesList.removeAll();

								dockerFilesToProcess.clear();
								RDFlist.removeAll();

								txtWelcomeToSwiprolog.setText("");
								
								textSparql.setText("");
								textSparql.setEnabled(false);
								btnButtonRunQuery.setEnabled(false);
								
								textSparqlOutput.setText("");
							}
						});
						mntmClear.setText("Clear");
				
				new MenuItem(menu_5, SWT.SEPARATOR);
				
				MenuItem mntmExit = new MenuItem(menu_5, SWT.NONE);
				mntmExit.addSelectionListener(new SelectionAdapter() 
				{
					/**
					 * EXIT clicked
					 */
					@Override
					public void widgetSelected(SelectionEvent e) 
					{
						shlDockerfilesToRdf.close();
					}
				});
				mntmExit.setText("Exit");

		MenuItem mntmDockerfiles = new MenuItem(menu, SWT.CASCADE);
		mntmDockerfiles.setText("Dockerfiles");

		Menu menu_1 = new Menu(mntmDockerfiles);
		mntmDockerfiles.setMenu(menu_1);

		MenuItem mntmLoad = new MenuItem(menu_1, SWT.NONE);
		mntmLoad.addSelectionListener(new SelectionAdapter()
		{
			/**
			 * LOAD clicked
			 */
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
					// dockerFiles.clear();
					// dockerFilesList.removeAll();

					for (String dockerFile : dialog.getFileNames())
					{
						String file = dialog.getFilterPath() + File.separator + dockerFile;
						dockerFiles.add(new File(file));
						dockerFilesList.add(file);
					}
				} else
				{
					// todo: cancelled
				}
			}
		});
		mntmLoad.setText("Load\tCtrl+O");
		mntmLoad.setAccelerator(SWT.MOD1 + 'O');

		MenuItem mntmNewItem = new MenuItem(menu_1, SWT.NONE);
		mntmNewItem.addSelectionListener(new SelectionAdapter()
		{
			/**
			 * PROCESS clicked
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (checkIfPrologPathExist())
				{
					if (dockerFilesList.getSelectionCount() == 0)
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
						String newFile = dockerFilesList.getItem(dockerFilesList.getSelectionIndices()[i])
								.replaceAll("\\\\sources\\\\", "\\\\rdf\\\\");
						dockerFilesToProcess.add(new File(newFile));
					}

					// TODO: remove duplicates
					dockerFilesToProcess.forEach(file -> RDFlist.add(file.getAbsolutePath()));
					
//					AppLoadingDialog loadingDialog = new AppLoadingDialog(Display.getCurrent().getActiveShell());
//					loadingDialog.open();
//					loadingDialog.
				
					for (int i = 1; i <= dockerFilesToProcess.size(); i++)
					{ 
						try
						{
							
//							loadingDialog.updateState(i / dockerFilesToProcess.size() * 100);
							System.out.println("Progress: " + ((float) i / (float) dockerFilesToProcess.size()) * 100);
							/*
							 * swipl.exe -s E:\DockerFilesToRDF\DockerFilesToRDF\dockerfiles.pl -g "main()."
							 * -q -t halt -- E:\DockerFilesToRDF\DockerFilesToRDF\sources\dockeronto
							 * E:\DockerFilesToRDF\DockerFilesToRDF\rdf\dockeronto
							 */
							ProcessBuilder PB = new ProcessBuilder(appProperties.getProperty("prolog_path"), "-v");
							PB.redirectErrorStream(true);
							Process P = PB.start();
//							P.waitFor();
	
							BufferedReader processOutput = new BufferedReader(new InputStreamReader(P.getInputStream()));
							// BufferedWriter processInput = new BufferedWriter(new
							// OutputStreamWriter(P.getOutputStream()));
	
							String line;
							StringBuilder outputMessage = new StringBuilder("");
							while ((line = processOutput.readLine()) != null)
							{
								outputMessage.append(line).append("\n");
							}
	//						System.out.println(outputMessage.toString());
							txtWelcomeToSwiprolog.append(outputMessage.toString() + "======\n");
	
	
	
						} 
						catch (IOException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
							// dialog.setMessage(e.getMessage());
						} 
					}
					
//					loadingDialog.close();

				}
			}
		});
		mntmNewItem.setText("Process\tCtrl+P");
		mntmNewItem.setAccelerator(SWT.MOD1 + 'P');

		MenuItem mntmRdf = new MenuItem(menu, SWT.CASCADE);
		mntmRdf.setText("RDF");

		Menu menu_2 = new Menu(mntmRdf);
		mntmRdf.setMenu(menu_2);

		MenuItem mntmNewItem_1 = new MenuItem(menu_2, SWT.NONE);
		mntmNewItem_1.addSelectionListener(new SelectionAdapter()
		{
			/*
			 * INSERT TO RDF DB clicked
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (RDFlist.getSelectionCount() == 0)
				{
					MessageBox dialog = new MessageBox(shlDockerfilesToRdf, SWT.ICON_ERROR | SWT.OK);
					dialog.setText("Error");
					dialog.setMessage("Please choose at least one RDF file to process!");
					dialog.open();
					return;
				}

				btnButtonRunQuery.setEnabled(true);
				textSparql.setEnabled(true);

				for (int i = 0; i < RDFlist.getSelectionIndices().length; i++)
				{
					rdfFilesToProcess.add(new File(RDFlist.getItem(RDFlist.getSelectionIndices()[i])));
					
					try (RepositoryConnection repositoryConnection = RDFDB.getConnection()) 
					{
						Reader reader = new FileReader(rdfFilesToProcess.get(i));
						
						Model model = Rio.parse(reader, "", RDFFormat.TURTLE);
						Rio.write(model, System.out, RDFFormat.RDFXML);
						
						repositoryConnection.add(reader, "", RDFFormat.TURTLE);

					} 
					catch (RDFParseException e1)
					{
						System.out.println("Error while parsing RDF file!");
						System.out.println("FILE: " + rdfFilesToProcess.get(i));
						System.out.println("LINE: " + e1.getLineNumber() + ":" + e1.getColumnNumber());
						System.out.println("MESSAGE: " + e1.getMessage());
					} 
					catch (UnsupportedRDFormatException e1)
					{
						e1.printStackTrace();
					}
					catch (IOException e1)
					{
						System.out.println("Problem with file!");
						System.out.println("FILE: " + rdfFilesToProcess.get(i));
						e1.printStackTrace();
					}
				}
				System.out.println("Files added to RDF DB...");
			}
		});
		mntmNewItem_1.setText("Insert to db");

		MenuItem mntmProlog = new MenuItem(menu, SWT.CASCADE);
		mntmProlog.setText("Prolog");

		Menu menu_3 = new Menu(mntmProlog);
		mntmProlog.setMenu(menu_3);

		MenuItem mntmSettings = new MenuItem(menu_3, SWT.NONE);
		mntmSettings.addSelectionListener(new ConfigurationSelectionListener(appProperties));
		mntmSettings.setText("Configuration");

		MenuItem mntmVersion = new MenuItem(menu_3, SWT.NONE);
		mntmVersion.addSelectionListener(new SelectionAdapter()
		{
			/*
			 * VERSION clicked
			 */
			@Override
			public void widgetSelected(SelectionEvent se)
			{
				if (checkIfPrologPathExist())
				{

					// create a dialog with ok and cancel buttons and a question icon
					MessageBox dialog = new MessageBox(shlDockerfilesToRdf, SWT.ICON_INFORMATION | SWT.OK);
					dialog.setText("Prolog version");

					try
					{
						Process P = new ProcessBuilder(appProperties.getProperty("prolog_path"), "--version").start();
						BufferedReader processOutput = new BufferedReader(new InputStreamReader(P.getInputStream()));
						// BufferedWriter processInput = new BufferedWriter(new
						// OutputStreamWriter(P.getOutputStream()));

						String line;
						StringBuilder outputMessage = new StringBuilder("");
						while ((line = processOutput.readLine()) != null)
						{
							outputMessage.append(line).append("\n");
						}
						dialog.setMessage(outputMessage.toString());

					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						dialog.setMessage(e.getMessage());
					}

					dialog.open();
				}

			}
		});
		mntmVersion.setText("Version");
		// mntmVersion.setAccelerator(SWT.MOD1 + 'V');

		MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
		mntmHelp.setText("Help");

		Menu menu_4 = new Menu(mntmHelp);
		mntmHelp.setMenu(menu_4);

		MenuItem mntmHelp_1 = new MenuItem(menu_4, SWT.NONE);
		mntmHelp_1.setEnabled(false);
		mntmHelp_1.setText("Help\tF1");
		mntmHelp_1.setAccelerator(SWT.F1);

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
		RDFlist.setBounds(10, 20, 361, 93);

		Group grpProlog = new Group(shlDockerfilesToRdf, SWT.NONE);
		GridData gd_grpProlog = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_grpProlog.widthHint = 767;
		grpProlog.setLayoutData(gd_grpProlog);
		grpProlog.setText("Prolog output:");

		txtWelcomeToSwiprolog = new Text(grpProlog, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		txtWelcomeToSwiprolog.setFont(SWTResourceManager.getFont("Consolas", 8, SWT.NORMAL));
		txtWelcomeToSwiprolog.setEditable(false);
		txtWelcomeToSwiprolog.setBounds(10, 21, 753, 100);

		Group grpSparql = new Group(shlDockerfilesToRdf, SWT.NONE);
		GridData gd_grpSparql = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_grpSparql.heightHint = 130;
		gd_grpSparql.widthHint = 763;
		grpSparql.setLayoutData(gd_grpSparql);
		grpSparql.setText("SPARQL:");

		textSparql = new Text(grpSparql, SWT.BORDER | SWT.V_SCROLL);
		textSparql.setText("PREFIX ex: <http://example.org/>\r\nPREFIX do: <http://linkedcontainers.org/vocab#>\r\nSELECT *\r\nWHERE {\r\n\t?s ?p ?o\r\n}");
		textSparql.setEnabled(false);
		textSparql.setBounds(10, 20, 749, 90);

		btnButtonRunQuery = new Button(grpSparql, SWT.NONE);
		btnButtonRunQuery.addSelectionListener(new SelectionAdapter()
		{
			/*
			 * RUN QUERY clicked
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				try (RepositoryConnection repositoryConnection = RDFDB.getConnection()) 
				{


				    
				    // A QueryResult is also an AutoCloseable resource, so make sure it gets
				    // closed when done.
				    try 
				    {
				    	TupleQuery query = repositoryConnection.prepareTupleQuery(textSparql.getText());
				    	TupleQueryResult result = query.evaluate();
				    	if (!result.hasNext())
				    	{
				    		 System.out.println("SPARQL: No results!");
				    		 textSparqlOutput.append("SPARQL: No results!\n");
				    		 textSparqlOutput.append("=====\n");
				    	}
				    	
						// we just iterate over all solutions in the result...
						while (result.hasNext()) 
						{
						    BindingSet solution = result.next();
						    solution.getBindingNames();
						    solution.iterator();
						    System.out.println("Foo");
						}
				    } 
				    catch (QueryEvaluationException e1)
				    {
				    	textSparqlOutput.append("SPARQL query error!\n");
				    	textSparqlOutput.append("CAUSE: " + e1.getCause() + "\n");
				    	textSparqlOutput.append("MESSAGE: " + e1.getMessage() + "\n");
						e1.getStackTrace();
				    }
				    catch (MalformedQueryException e1)
				    {
						MessageBox dialog = new MessageBox(shlDockerfilesToRdf, SWT.ICON_ERROR | SWT.OK);
						dialog.setText("SPARQL query error");
						dialog.setMessage(e1.getMessage());
						
						textSparqlOutput.append("SPARQL query error!\n");
						textSparqlOutput.append(e1.getMessage() + "\n");
						textSparqlOutput.append("=====\n");
						
						dialog.open();
				    }
				}
			}
		});
		btnButtonRunQuery.setEnabled(false);
		btnButtonRunQuery.setBounds(10, 114, 75, 25);
		btnButtonRunQuery.setText("Run query");
		
		Group grpSparqlOutput = new Group(shlDockerfilesToRdf, SWT.NONE);
		GridData gd_grpSparqlOutput = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_grpSparqlOutput.heightHint = 105;
		gd_grpSparqlOutput.widthHint = 767;
		grpSparqlOutput.setLayoutData(gd_grpSparqlOutput);
		grpSparqlOutput.setText("SPARQL output:");
		
		textSparqlOutput = new Text(grpSparqlOutput, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		textSparqlOutput.setFont(SWTResourceManager.getFont("Consolas", 8, SWT.NORMAL));
		textSparqlOutput.setBounds(10, 21, 753, 90);

		checkIfPrologPathExist();
	}

	private Boolean checkIfPrologPathExist()
	{
		if (appProperties.getProperty("prolog_path") == null)
		{
			MessageBox dialog = new MessageBox(shlDockerfilesToRdf, SWT.ICON_ERROR | SWT.OK);
			dialog.setText("Prolog configuration error");
			dialog.setMessage("You need to specify path to prolog in Prolog -> Configuration!");
			dialog.open();

			return false;
		}
		return true;

	}
}
