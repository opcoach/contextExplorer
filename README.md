Eclipse 4 context Spy
======================
  
 March 2014 changes
 ------------------
 
 The master branch can be used (contains now the keyboard shortcut to open it : ALT SHIFT F10)
 
 This short cut will be available soon in the latest version of e4 tools (when review will be completed)
 
 You can add this plugin in your launch configuration and the short cut will be available.
 


  January 2014 changes 
  ---------------------
  
    This code is now moving to Eclipse 4 tools. See : 
    
         <li>Bug     : https://bugs.eclipse.org/bugs/show_bug.cgi?id=422543</li>
         
         <li>Ipzilla : https://dev.eclipse.org/ipzilla/show_bug.cgi?id=7865 </li>
    
    Projects and packages have been renamed. 
    
    The CQ7865 branch contains the code proposal for Eclipse. 
    
    In this new branch, you must now refer to ContextSpyPart instead of ContextExplorerPart :
       bundleclass://org.eclipse.e4.tools.context.spy/org.eclipse.e4.tools.context.spy.ContextSpyPart
       
       
       

A context Explorer for Eclipse 4 provided by OPCoach

Just checkout this project in your workspace, make your project depend on it optionaly, and you can add the :

bundleclass://com.opcoach.e4.contextExplorer/com.opcoach.e4.contextExplorer.parts.ContextExplorerPart

in your application to debug it.

You can find also more information on the OPCoach blog :  

<li>in french : http://www.opcoach.com/2013/03/eclipse-4-context-explorer/</li>

<li>in english : http://www.opcoach.com/en/2013/03/eclipse-4-context-explorer/</li>
