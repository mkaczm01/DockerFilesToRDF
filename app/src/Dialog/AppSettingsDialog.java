package Dialog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;

import SelectionListener.BrowseSwiplSelectionListener;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class AppSettingsDialog extends Dialog
{
	private Text textPrologPath;
	
	private Properties appProperties;

	private String prologPath;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public AppSettingsDialog(Shell parentShell)
	{
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite container = (Composite) super.createDialogArea(parent);

		Group grpPathToProlog = new Group(container, SWT.NONE);
		GridData gd_grpPathToProlog = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_grpPathToProlog.widthHint = 419;
		grpPathToProlog.setLayoutData(gd_grpPathToProlog);
		grpPathToProlog.setText("Path to Prolog");
		
		textPrologPath = new Text(grpPathToProlog, SWT.BORDER);
		textPrologPath.setBounds(10, 20, 324, 21);
		
		setPrologPath(appProperties.getProperty("prolog_path", "Choose one..."));
		textPrologPath.setText(prologPath);
		
		Button btnPrologButton = new Button(grpPathToProlog, SWT.NONE);
		btnPrologButton.addSelectionListener(new BrowseSwiplSelectionListener(this));
		btnPrologButton.setBounds(340, 18, 75, 25);
		btnPrologButton.setText("Browse");

		return container;
	}

	public void setPrologPath(String v)
	{
		if (this.prologPath == v)
			return;
		
		textPrologPath.setText(v);
		this.prologPath = v;
	}
	
	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		button.addSelectionListener(new SelectionAdapter()
		{
			/**
			 * OK clicked
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{

				System.out.print(prologPath);
				File configFile = new File("app.settings");
				FileWriter writer;
				appProperties.setProperty("prolog_path", prologPath);//textPrologPath.getText());
				try
				{
					writer = new FileWriter(configFile);
					appProperties.store(writer, "App settings");
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(450, 150);
	}

	public void setProperties(Properties p)
	{
		appProperties = p;	
	}
}
