package Dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class AppLoadingDialog extends Dialog
{
	private Text text;
	private ProgressBar progressBar;
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AppLoadingDialog(Shell parentShell)
	{
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		
		Label labelDialogMessage = new Label(container, SWT.NONE);
		labelDialogMessage.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		labelDialogMessage.setText("Trwa przetwarzanie plik\u00F3w Docker przez Prologa...");
		
		progressBar = new ProgressBar(container, SWT.NONE);
		GridData gd_progressBar = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_progressBar.widthHint = 434;
		progressBar.setLayoutData(gd_progressBar);
//		progressBar.setSelection(45);
		progressBar.setState(45);
		
		text = new Text(container, SWT.BORDER | SWT.V_SCROLL);
		text.setEditable(false);
		GridData gd_text = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_text.heightHint = 67;
		gd_text.widthHint = 435;
		text.setLayoutData(gd_text);

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		//createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		//createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(450, 300);
	}
	
	public void updateState(int state)
	{
		this.progressBar.setState(state);
	}

}
