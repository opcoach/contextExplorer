contextExplorer
===============

  ------------------  January 2014 changes ------------------------
    This code is now moving to Eclipse 4 tools. See : 
         https://bugs.eclipse.org/bugs/show_bug.cgi?id=422543
         https://dev.eclipse.org/ipzilla/show_bug.cgi?id=7865 
    
    Projects and packages have been renamed. 
    
    The CQ7865 branch contains the code proposal for Eclipse. 
    
    In this new branch, you must now refer to ContextSpyPart instead of ContextExplorerPart :
       bundleclass://org.eclipse.e4.tools.context.spy/org.eclipse.e4.tools.context.spy.ContextSpyPart
  -----------------------------------------------------------------


A context Explorer for Eclipse 4 provided by OPCoach

Just checkout this project in your workspace, make your project depend on it optionaly, and you can add the :

bundleclass://com.opcoach.e4.contextExplorer/com.opcoach.e4.contextExplorer.parts.ContextExplorerPart

in your application to debug it.

You can find also more information on the OPCoach blog :  

<li>in french : http://www.opcoach.com/2013/03/eclipse-4-context-explorer/</li>

<li>in english : http://www.opcoach.com/en/2013/03/eclipse-4-context-explorer/</li>
