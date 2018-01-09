package SelectionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;

import Dialog.AppSettingsDialog;

public class ConfigurationSelectionListener implements SelectionListener
{
	protected Properties appProperties;
	
	public ConfigurationSelectionListener(Properties p)
	{
		appProperties = p;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * CONFIGURATION clicked
	 */
	@Override
	public void widgetSelected(SelectionEvent e)
	{
		AppSettingsDialog dialog = new AppSettingsDialog(Display.getCurrent().getActiveShell());
		dialog.setProperties(appProperties);
		dialog.open();
	}
}