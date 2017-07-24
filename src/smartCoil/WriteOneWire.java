package smartCoil;

/*---------------------------------------------------------------------------
 * Copyright (C) 1999,2000 Dallas Semiconductor Corporation, All Rights Reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY,  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL DALLAS SEMICONDUCTOR BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * Except as contained in this notice, the name of Dallas Semiconductor
 * shall not be used except as stated in the Dallas Semiconductor
 * Branding Policy.
 *---------------------------------------------------------------------------
 */

import java.util.*;
import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;
import java.lang.InterruptedException;


/**
 * Writes a string to block for a one-wire device
 */
public class WriteOneWire
{

   /**
    * Main for 1-Wire Write-memory utility
    */
   public static void main (String args [])
   {
      Vector<OneWireContainer> owd_vect = new Vector<OneWireContainer>(5);
      OneWireContainer owd;
      int              addr;
      DSPortAdapter    adapter = null;
      MemoryBank       bank;
      byte[]           data;
      String input_string = args[0];
      System.out.println();
      System.out.println(
         "Starting 1-Wire write-memory utility...");

      try
      {

         // get the default adapter
         adapter = OneWireAccessProvider.getDefaultAdapter();

         // adapter driver info
         System.out.println(
            "=========================================================================");
         System.out.println("== Adapter Name: " + adapter.getAdapterName());
         System.out.println("== Adapter Port description: "
                            + adapter.getPortTypeDescription());
         System.out.println("== Adapter Version: "
                            + adapter.getAdapterVersion());
         System.out.println(
                 "=========================================================================");

         // Get exclusive use of adapter
         adapter.beginExclusive(true);

         // Loop to do menu
	
	     // Find all parts
	     owd_vect = findAllDevices(adapter);
	
	     // Select a device
	     owd = selectDevice(owd_vect);
	
	     // Select a bank
	     bank = selectBank(owd);
	     
	     // Write block
	     addr = 0;
	     data = getData_input(input_string);
	     bankWriteBlock(bank, data, addr);
	      
	     // Release adapter port
	     if (adapter != null)
	     {
	
	          // End exclusive use of adapter
	          adapter.endExclusive();
	
	          // Free the port used by the adapter
	          System.out.println("Releasing adapter port...");
	
	          try
	          {
	             adapter.freePort();
	          }
	          catch (OneWireException e)
	          {
	             System.out.println(e);
	          }
	     }
	     System.out.println("Adapter released successfully.");
	     return;
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Search for all devices on the provided adapter and return
    * a vector
    *
    * @param  adapter valid 1-Wire adapter
    *
    * @return Vector or OneWireContainers
    */
   public static Vector<OneWireContainer> findAllDevices (DSPortAdapter adapter)
   {
      Vector<OneWireContainer>           owd_vect = new Vector<OneWireContainer>(3);
      OneWireContainer owd;

      try
      {

         // Clear any previous search restrictions
         adapter.setSearchAllDevices();
         adapter.targetAllFamilies();
         adapter.setSpeed(DSPortAdapter.SPEED_REGULAR);

         // Enumerate through all the 1-Wire devices and collect them in a vector
         for (Enumeration<?> owd_enum = adapter.getAllDeviceContainers();
                 owd_enum.hasMoreElements(); )
         {
            owd = ( OneWireContainer ) owd_enum.nextElement();

            owd_vect.addElement(owd);

            // Set owd to max possible speed with available adapter, allow fall back
            if (adapter.canOverdrive()
                    && (owd.getMaxSpeed() == DSPortAdapter.SPEED_OVERDRIVE))
               owd.setSpeed(owd.getMaxSpeed(), true);
         }

      }
      catch (Exception e)
      {
         System.out.println(e);
      }

      return owd_vect;
   }

   //--------
   //-------- Write methods
   //--------

   /**
    * Write a block of data with the provided MemoryBank.
    *
    * @param  bank  MemoryBank to write block to
    * @param  data  data to write in a byte array
    * @param  addr  address to start the write
    */
   public static void bankWriteBlock (MemoryBank bank, byte[] data, int addr)
   {
      try
      {
         bank.write(addr, data, 0, data.length);
         System.out.println();
         System.out.println("wrote block length " + data.length + " at addr "
                            + addr);
      }
      catch (Exception e)
      {
         System.out.println(e);
      }
   }

   /**
    * Create a menu from the provided OneWireContainer
    * Vector and allow the user to select a device.
    *
    * @param  owd_vect vector of devices to choose from
    *
    * @return OneWireContainer device selected
    */
   public static OneWireContainer selectDevice (Vector<OneWireContainer> owd_vect)
      throws InterruptedException
   {

      // create a menu
      String[]         menu = new String [owd_vect.size() + 2];
      OneWireContainer owd;
      int              i;

      menu [0] = "Device Selection";

      for (i = 0; i < owd_vect.size(); i++)
      {
         owd          = ( OneWireContainer ) owd_vect.elementAt(i);
         menu [i + 1] = new String("(" + i + ") " + owd.getAddressAsString()
                                   + " - " + owd.getName());

         if (owd.getAlternateNames().length() > 0)
            menu [i + 1] += "/" + owd.getAlternateNames();
      }

      menu [i + 1] = new String("[" + i + "]--Quit");

     
      int select = 1;
      if (select == i)
         throw new InterruptedException("Quit in device selection");

      return ( OneWireContainer ) owd_vect.elementAt(select);
   }

   /**
    * Create a menu of memory banks from the provided OneWireContainer
    * allow the user to select one.
    *
    * @param  owd devices to choose a MemoryBank from
    *
    * @return MemoryBank memory bank selected
    */
   public static MemoryBank selectBank (OneWireContainer owd)
      throws InterruptedException
   {

      // create a menu
      Vector<MemoryBank>     banks = new Vector<MemoryBank>(3);
      int        i;

      // get a vector of the banks
      for (Enumeration<?> bank_enum = owd.getMemoryBanks();
              bank_enum.hasMoreElements(); )
      {
         banks.addElement(( MemoryBank ) bank_enum.nextElement());
      }

      String[] menu = new String [banks.size() + 2];

      menu [0] = "Memory Bank Selection for " + owd.getAddressAsString()
                 + " - " + owd.getName();

      if (owd.getAlternateNames().length() > 0)
         menu [0] += "/" + owd.getAlternateNames();

      for (i = 0; i < banks.size(); i++)
      {
         menu [i + 1] = new String(
            "(" + i + ") "
            + (( MemoryBank ) banks.elementAt(i)).getBankDescription());
      }
      menu [i + 1]    = new String("[" + i + "]--Quit");


      int select = 0;
      if (select == i)
         throw new InterruptedException("Quit in bank selection");

      return ( MemoryBank ) banks.elementAt(select);
   }

   public static byte[] getData_input (String tstr)
   {
      byte[]  data = null;
      data = tstr.getBytes();

      System.out.print("Data to write :");
      hexPrint(data, 0, data.length);
      System.out.println();

      return data;
   }

   //--------
   //-------- Display Methods
   //--------

   /**
    * Print an array of bytes in hex to standard out.
    *
    * @param  dataBuf data to print
    * @param  offset  offset into dataBuf to start
    * @param  len     length of data to print
    */
   public static void hexPrint (byte[] dataBuf, int offset, int len)
   {
      for (int i = 0; i < len; i++)
      {
         if ((dataBuf [i + offset] & 0x000000FF) < 0x00000010)
         {
            System.out.print("0");
            System.out.print(Integer.toHexString(( int ) dataBuf [i + offset]
                                                 & 0x0000000F).toUpperCase());
         }
         else
            System.out.print(Integer.toHexString(( int ) dataBuf [i + offset]
                                                 & 0x000000FF).toUpperCase());
      }
   }
}