/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
MVC "Controller" module for ApplicationCPUclk,
note MVC is Model, View, Controller paradigm.
Application: CPU Clock info.
Controller interconnects model and view.
Note, variables bm (BuildModel), bv (BuiltView) located at parent class.
*/

package cpuid.applications.applicationcpuclk;

import cpuid.applications.mvc.BCA;
import cpuid.About;

public class BuiltController extends BCA   //  implements BC 
{
public BuiltController()
    {
    // MVC initialization
    bm = new BuiltModel();
    bv = new BuiltView( bm, About.getX2size(), About.getY2size() );
    }
}
