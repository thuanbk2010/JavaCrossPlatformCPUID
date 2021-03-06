/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
CPUID driver component:
CPUID standard function 00000016h declared as CPR.COMMAND.
THIS MODULE IS EXPERIMENTAL, NOT VERIFIED, NOT CONNECTED YET.
make visual maximum sub-leaf functions, see other functions with
same subleaf declare mechanism.
TODO: add sub-functions 1-3.
*/

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;
import cpuid.kernel.IOPB;

public class CPUID00000017 extends CommandAdapter 
{
// CPUID function full name
private static final String F_NAME =
        "System-On-Chip vendor attribute enumeration";

// Parameters table up string
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };

// Return if function not found
private final static String[][] NO_RESULT =
    {
        { "n/a" , "" , "" , "" , "" }
    };

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    {
        { "Maximum SOCID index" , 31 , 0 }
    };
private final static Object[][] DECODER_EBX =
    {
        { "SOC Vendor ID" , 15 , 0     } ,
        { "Is Vendor Scheme" , 16 , 16 }
    };
private final static Object[][] DECODER_ECX =
    {
        { "Project ID" , 31 , 0 }
    };
private final static Object[][] DECODER_EDX =
    {
        { "Stepping ID" , 31 , 0 }
    };

// Calculate control data total size for output formatting
private final static int NX   = COMMAND_UP_1.length;
private final static int NY1  = DECODER_EAX.length + 0;
private final static int NY2  = DECODER_EBX.length + 0;
private final static int NY3  = DECODER_ECX.length + 0;
private final static int NY4  = DECODER_EDX.length + 1;
private final static int NY  = NY1 + NY2 + NY3 + NY4 + 1;

// Return CPUID this function full name
// INPUT:   Reserved array
// OUTPUT:  String, CPUID function full name
@Override public String getCommandLongName(long[] dummy ) 
    { return F_NAME; }

// Return CPUID this function parameters table up string
// INPUT:   Reserved array
// OUTPUT:  String, CPUID function details table up string
@Override public String[] getCommandUp1( long[] dummy )
    { return COMMAND_UP_1; }

// Build and return CPUID this function detail information table
// INPUT:   Binary array = CPUID dump data
// OUTPUT:  Array of strings = CPUID this function detail information table
@Override public String[][] getCommandText1( long[] array )
    {
    // Scan binary dump, find entry for this function
    int x1 = CPUID.findFunction( array, 0x00000017 );
    // Return "n/a" if this function entry not found
    if (x1<0) { return NO_RESULT; }
    // Build and pre-blank result text array
    String[][] result = new String[NY][NX];  // Text formatted by control data
    for (int i=0; i<NY; i++)  // Cycle for rows 
        { 
        for(int j=0; j<NX; j++)  // Cycle for columns
            { 
            result[i][j]=""; 
            } 
        }
    
    // Results of subfunction 0: numeric parameters
    // Parameters from CPUID dump, EAX register
    int p=0;  // pointer for sequentally store strings in the table
    int y = (int) (array[x1+2] & (((long)((long)(-1)>>>32))) );      // y = EAX
    int[] z = CPUID.decodeBitfields ( "EAX" , DECODER_EAX , y , result , p );
    int subMaximum = z[0];  // used later as subfunctions maximum number
    // Parameters from CPUID dump, EBX register
    p = NY1;
    y = (int) ( array[x1+2] >>> 32 );                                // y = EBX
    z = CPUID.decodeBitfields ( "EBX" , DECODER_EBX , y , result , p );
    if ( z[1]==0 )
        { result[p][4] = "Assigned by Intel"; }
    else
        { result[p][4] = "Industry standard enumeration"; }
    // Parameters from CPUID dump, ECX register
    p = NY1+NY2;
    y = (int) ( array[x1+3] & (((long)((long)(-1)>>>32))) );         // y = ECX
    CPUID.decodeBitfields ( "ECX" , DECODER_ECX , y , result , p );
    // Parameters from CPUID dump, EDX register
    p = NY1+NY2+NY3;
    y = (int) ( array[x1+3] >>> 32 );                                // y = EDX
    CPUID.decodeBitfields ( "EDX" , DECODER_EDX , y , result , p );

    // Results of subfunction 1: text string
    p = NY1+NY2+NY3+NY4;
    result[p][0] = "SOC Vendor Brand String";
    result[p][4] = "n/a";
    // Stop if maximum subfunction number is invalid
    if ((subMaximum<=0)|(subMaximum>3)) { return result; }
    // Cycle for extract text string from subfunctions sequence
    for(int i=0; i<=subMaximum; i++)
        {
        y = (int) ( array[x1+0] & (((long)((long)(-1)>>>32))) );  // y = fnc.
        if ( y != 0x00000017 ) { break; }   // Break if end of function entries
        String s = IOPB.receiveString( array, x1+2, 2 );  // EAX,EBX,ECX,EDX
        result[p][4] = result[p][4] + s;   // Add substring by current subfnc.
        x1 += 4;  // Next subfunction entry
        }
    result[p][4] = result[p][4].trim();
    
    // Result is ready, all strings filled    
    return result;
    }
}

