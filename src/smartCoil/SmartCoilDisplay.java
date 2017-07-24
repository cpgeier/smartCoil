package smartCoil;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Text;

import com.dalsemi.onewire.OneWireAccessProvider;
import com.dalsemi.onewire.adapter.DSPortAdapter;
import com.dalsemi.onewire.container.OneWireContainer;

import org.eclipse.swt.widgets.Label;
import java.util.*;
import java.util.Enumeration;


public class SmartCoilDisplay {

	protected Shell shell;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SmartCoilDisplay window = new SmartCoilDisplay();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		System.out.print("Starting smartCoil...\n");
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	
	/**
	 * Converts hex to java.awt.color
	 * @param display
	 * @param hexString
	 * @return
	 */
	public Color decode(Display display, String hexString)
	{
	    try
	    {
	        java.awt.Color c = java.awt.Color.decode(hexString);

	        return new Color(display, c.getRed(), c.getGreen(), c.getBlue());
	    }
	    catch(NumberFormatException e)
	    {
	        return null;
	    }
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell(SWT.SHELL_TRIM & (~SWT.RESIZE) & (~SWT.MAX));
		shell.setSize(345, 342);
		shell.setText("smartCoil - Administator Tool");
		shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		// Default name and port values
		Image icon = new Image(null, "icon.png");
		String AdapterName = "Not Initizalized";
	    String AdapterPort = "Not Initizalized";
		
	    shell.setImage(icon);
	    
	    // Verifies device is connected and stores name and port
	    OneWireContainer owd;
		try
		  {
		     // get the default adapter  
		     DSPortAdapter adapter = OneWireAccessProvider.getDefaultAdapter();
		
		     AdapterName = adapter.getAdapterName();
		     AdapterPort = adapter.getPortName();
		     
		     System.out.println();
		     System.out.println("Adapter: " + adapter.getAdapterName()
		                        + " Port: " + adapter.getPortName());
		     System.out.println();
		     
		     // get exclusive use of adapter
		     adapter.beginExclusive(true);
		
		     // clear any previous search restrictions
		     adapter.setSearchAllDevices();
		     adapter.targetAllFamilies();
		     adapter.setSpeed(DSPortAdapter.SPEED_REGULAR);
		
		     // enumerate through all the 1-Wire devices found
		     for (Enumeration<?> owd_enum = adapter.getAllDeviceContainers();
		             owd_enum.hasMoreElements(); )
		     {
		        owd = ( OneWireContainer ) owd_enum.nextElement();
		
		        System.out.println(owd.getAddressAsString());
		     }
		
		     // end exclusive use of adapter
		     adapter.endExclusive();
		
		     // free port used by adapter
		     adapter.freePort();
		  }
		  catch (Exception e)
		  {
		     System.out.println(e);
		  }
		
		// top-left innerspec logo
		Image logo = new Image(null, "logo_small.png");
		
		// Adapter and port labels
		Label lblAdapter = new Label(shell, SWT.NONE);
		lblAdapter.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblAdapter.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BORDER));
		lblAdapter.setBounds(159, 8, 55, 15);
		lblAdapter.setText("Adapter: ");
		
		Label lblPort = new Label(shell, SWT.NONE);
		lblPort.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblPort.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BORDER));
		lblPort.setBounds(159, 25, 36, 15);
		lblPort.setText("Port:");
		
		// Adapter id: {DS9490}
		final Label lblNotScanned = new Label(shell, SWT.NONE);
		lblNotScanned.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNotScanned.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BORDER));
		lblNotScanned.setBounds(220, 25, 92, 15);
		lblNotScanned.setText("Not Scanned");
		lblNotScanned.setText(AdapterPort);
		
		// Adapter port: USB1
		final Label lblNotScanned_1 = new Label(shell, SWT.NONE);
		lblNotScanned_1.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNotScanned_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BORDER));
		lblNotScanned_1.setText("Not Scanned");
		lblNotScanned_1.setBounds(220, 8, 78, 15);
		
		// Sets adapter id
		lblNotScanned_1.setText(AdapterName);
		
		// Top-left logo
		Label lblLogo = new Label(shell, SWT.NONE);
		lblLogo.setBounds(25, 10, 120, 25);
		lblLogo.setText("Logo");
		lblLogo.setImage(logo);
		lblLogo.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BORDER));
		
		// Creates dark-grey header background
		Label header_background = new Label(shell, SWT.NONE);
		header_background.setBounds(0, 0, 337, 47);
		header_background.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BORDER));	
		
		// Creates information title
		Label lblCoilInformation = new Label(shell, SWT.NONE);
		lblCoilInformation.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCoilInformation.setBounds(109, 61, 92, 15);
		lblCoilInformation.setText("Coil Information");
		
		// Labels for editable information
		Label lblUseCounter = new Label(shell, SWT.NONE);
		lblUseCounter.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblUseCounter.setBounds(52, 124, 78, 15);
		lblUseCounter.setText("Use Counter: ");
		
		Label lblSystemIdentifier = new Label(shell, SWT.NONE);
		lblSystemIdentifier.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblSystemIdentifier.setBounds(51, 93, 55, 15);
		lblSystemIdentifier.setText("Coil ID:  ");
		
		Label lblManufactureDate = new Label(shell, SWT.NONE);
		lblManufactureDate.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblManufactureDate.setBounds(51, 153, 110, 15);
		lblManufactureDate.setText("Manufacture date:");
		
		Label lblCombatibilityId = new Label(shell, SWT.NONE);
		lblCombatibilityId.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCombatibilityId.setBounds(51, 184, 97, 15);
		lblCombatibilityId.setText("Compatibility ID: ");
		
		
		final Label lblDRM = new Label(shell, SWT.NONE);
		lblDRM.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblDRM.setBounds(51, 210, 55, 15);
		lblDRM.setText("DRM:");
		
		final Label lblNotification = new Label(shell, SWT.NONE);
		lblNotification.setAlignment(SWT.CENTER);
		lblNotification.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNotification.setBounds(52, 282, 226, 25);
		
		// Input boxes for editable information
		final Text coil_ID_input = new Text(shell, SWT.NONE);
		coil_ID_input.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		coil_ID_input.setBounds(186, 93, 92, 15);
		coil_ID_input.setText("");
		
		final Text use_counter_input = new Text(shell, SWT.NONE);
		use_counter_input.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		use_counter_input.setBounds(186, 124, 55, 15);
		use_counter_input.setText("");
		
		final Text manufacture_date_input = new Text(shell, SWT.NONE);
		manufacture_date_input.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		manufacture_date_input.setBounds(186, 153, 92, 15);
		manufacture_date_input.setText("");
		
		final Text compatibility_id_input = new Text(shell, SWT.NONE);
		compatibility_id_input.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		compatibility_id_input.setBounds(186, 184, 55, 15);
		compatibility_id_input.setText("");
		
		final Text DRM_input = new Text(shell, SWT.PASSWORD);
		DRM_input.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		DRM_input.setBounds(186, 210, 78, 15);
		DRM_input.setText("");
		
		// Creates read memory button
		Button btnReadMemory = new Button(shell, SWT.NONE);
		btnReadMemory.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent arg0) {
				// Clear input boxes
				coil_ID_input.setText("");
			    use_counter_input.setText("");
			    manufacture_date_input.setText("");
			    compatibility_id_input.setText("");
			    DRM_input.setText("");
			    lblNotification.setText("Reading data from One Wire Device...");
			    // Dump of raw string from memory
				List<String> all_responses = ReadOneWire.main(new String[]{"r"});
				String raw_string = all_responses.get(0);
				StringBuilder output = new StringBuilder();
			    // Convert from hex to string
				for (int i = 0; i < raw_string.length(); i+=2) {
			        String str = raw_string.substring(i, i+2);
			        output.append((char)Integer.parseInt(str, 16));
			    }
			    // Split using separator
			    String[] string_array = output.toString().split("-");
			    // Set input boxes to current values
			    coil_ID_input.setText(string_array[0]);
			    use_counter_input.setText(string_array[1]);
			    manufacture_date_input.setText(string_array[2]);
			    compatibility_id_input.setText(string_array[3]);
			    DRM_input.setText(string_array[4]);
			    lblNotification.setText("Read successful.");
			}
		});
		btnReadMemory.setBounds(52, 247, 96, 25);
		btnReadMemory.setText("Read Memory");
		
		// Creates write changes button
		Button btnWriteChanges = new Button(shell, SWT.NONE);
		btnWriteChanges.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent arg0) {
				lblNotification.setText("Writing changes to One Wire Device...");
				StringBuilder stringBuilder = new StringBuilder();
				// Creates a stringBuilder object that stores text from input boxes
				stringBuilder.append(coil_ID_input.getText() + "-");
				stringBuilder.append(use_counter_input.getText() + "-");
				stringBuilder.append(manufacture_date_input.getText() + "-");
				stringBuilder.append(compatibility_id_input.getText() + "-");
				stringBuilder.append(DRM_input.getText() + "-");
				String final_string = stringBuilder.toString();
				// Check that write won't overflow device memory
				if (final_string.length() < 100)
				{	
					System.out.println("\n	Writing \"" + final_string + "\" to memory.");
					// Writes string to memory
					WriteOneWire.main(new String[]{final_string});
				}
				else {
					lblNotification.setText("Write length must be below 100.");
				}
				lblNotification.setText("Write successful.");
			}
		});
		btnWriteChanges.setBounds(186, 247, 92, 25);
		btnWriteChanges.setText("Write Changes");

	}
}