
import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class AppSettingsDialog extends Dialog
{
	private Text textPrologPath;

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

		Button btnPrologButton = new Button(grpPathToProlog, SWT.NONE);
		btnPrologButton.addSelectionListener(new SelectionAdapter()
		{
			/**
			 * BROWSE clicked
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
			    dialog.setText("Select the path to swipl.exe ");
			    // TODO: Debug purposes only
			    dialog.setFilterPath("E:\\Program Files\\swipl");
			    
			    dialog.setFileName("swipl.exe");
			    dialog.setFilterNames(new String[] { "Prolog swipl.exe file)"});
			    dialog.setFilterExtensions(new String[] { "*.exe" });
	    
			    dialog.open();
			    
			}
		});
		btnPrologButton.setBounds(340, 18, 75, 25);
		btnPrologButton.setText("Browse");

		return container;
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
}
