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


public class ReadOneWire
{

   /**
    * Main for OWDump
    */
   public static List<String> main (String args [])
   {
      List<String> terms = new ArrayList<String>();
	  System.out.println();
      System.out.println(
         "Starting 1-Wire read-memory utility...");

      try
      {

         // get the default adapter  
         DSPortAdapter adapter = OneWireAccessProvider.getDefaultAdapter();

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

         // get exclusive use of adapter
         adapter.beginExclusive(true);

         // clear any previous search restrictions
         adapter.setSearchAllDevices();
         adapter.targetAllFamilies();
         adapter.setSpeed(DSPortAdapter.SPEED_REGULAR);

         // enumerate through all the iButtons found
         for ( Enumeration<?> owd_enum = adapter.getAllDeviceContainers(); owd_enum.hasMoreElements(); )
         {

            // get the next owd
            OneWireContainer owd =
               ( OneWireContainer ) owd_enum.nextElement();

            // set owd to max possible speed with available adapter, allow fall back
            if (adapter.canOverdrive()
                    && (owd.getMaxSpeed() == DSPortAdapter.SPEED_OVERDRIVE))
               owd.setSpeed(owd.getMaxSpeed(), true);
            
            // dump raw contents of all memory banks
            String[] temp_strings = dumpDeviceRaw(owd, (args.length == 1));
            for (int i = 0; i < temp_strings.length; i++){
         	    // converts dump to List<String>
            	if (temp_strings[i] != null){
        	   		terms.add(temp_strings[i]);
        	    }
            }

         }

         // end exclusive use of adapter
         adapter.endExclusive();

         // free the port used by the adapter
         System.out.println("Releasing adapter port...");
         adapter.freePort();
      }
      catch (Exception e)
      {
         System.out.println( "Exception: " + e );
         e.printStackTrace();
      }

      System.out.println("Adapter released successfully.");
      return terms;
   }

   /**
    * Dump all of the 1-Wire readable memory in the provided
    * MemoryContainer instance.
    *
    * @parameter owd device to check for memory banks.
    * @parameter showContents flag to indicate if the memory bank contents will
    *                      be displayed
    */
   public static String[] dumpDeviceRaw (OneWireContainer owd,
                                     boolean showContents)
   {
      byte[]  read_buf;
      boolean found_bank = false;
      int     i, reps = 10;
      int 	  count = 0;
      String[] all_final = new String[2]; // only two devices allowed
      
      // loop through all of the memory banks on device
      // get the port names we can use and try to open, test and close each
      for (Enumeration<?> bank_enum = owd.getMemoryBanks();
              bank_enum.hasMoreElements(); )
      {

         // get the next memory bank
         MemoryBank bank = ( MemoryBank ) bank_enum.nextElement();

         // found a memory bank
         found_bank = true;

         try
         {
            read_buf = new byte [bank.getSize()];

            // get overdrive going so not a factor in time tests
            bank.read(0, false, read_buf, 0, 1);

            // dynamically change number of reps
            reps = 1500 / read_buf.length;

            if (owd.getMaxSpeed() == DSPortAdapter.SPEED_OVERDRIVE)
               reps *= 2;

            if ((reps == 0) || showContents)
               reps = 1;

            if (!showContents)
               System.out.print("[" + reps + "]");

            // read the entire bank
            for (i = 0; i < reps; i++)
               bank.read(0, false, read_buf, 0, bank.getSize());

            if (showContents)
            {
               all_final[count] = hexPrint(read_buf, 0, bank.getSize());
               System.out.println("");
            }
         }
         catch (Exception e)
         {
            System.out.println("Exception in reading raw: " + e
                               + "  TRACE: ");
            e.printStackTrace();
         }
         
         count = count + 1;
      }

      if (!found_bank)
         System.out.println("XXXX Does not contain any memory bank's");
      return all_final;
   }

   /**
    * Print an array of bytes in hex to standard out.
    *
    * @param  dataBuf data to print
    * @param  offset  offset into dataBuf to start
    * @param  len     length of data to print
    */
   public static String hexPrint (byte[] dataBuf, int offset, int len)
   {
      StringBuilder stringBuilder = new StringBuilder();
	   for (int i = 0; i < len; i++)
      {
         if ((dataBuf [i + offset] & 0x000000FF) < 0x00000010)
         {
            System.out.print("0");
            System.out.print(Integer.toHexString(( int ) dataBuf [i + offset]
                                                 & 0x0000000F).toUpperCase());
            stringBuilder.append("0" + Integer.toHexString(( int ) dataBuf [i + offset]
                                                 & 0x0000000F).toUpperCase());
            
         }
         else
            System.out.print(Integer.toHexString(( int ) dataBuf [i + offset]
                                                 & 0x000000FF).toUpperCase());
	         stringBuilder.append(Integer.toHexString(( int ) dataBuf [i + offset]
                     & 0x000000FF).toUpperCase());  	
      }
	  String final_string = stringBuilder.toString();
	  return final_string;
   }
}
