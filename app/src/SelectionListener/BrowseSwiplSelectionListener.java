package SelectionListener;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

import Dialog.AppSettingsDialog;

public class BrowseSwiplSelectionListener implements SelectionListener
{
	public String prologPath;
	
	private Text textPrologPath;
	
	private AppSettingsDialog appSettingsDialog;
	
//	private Properties appProperties;
//	
//	public BrowseSwiplSelectionListener(Properties p)
//	{
//		appProperties = p;
//	}

	
	public BrowseSwiplSelectionListener(Text t)
	{
		textPrologPath = t;
	}
	
	public BrowseSwiplSelectionListener(String s)
	{
		prologPath = s;
	}

	public BrowseSwiplSelectionListener(AppSettingsDialog a)
	{
		appSettingsDialog = a;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}	
	
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
	    
	    prologPath = dialog.open();
	    appSettingsDialog.setPrologPath(prologPath);
	}
}
