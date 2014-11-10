FallenFeather
=============

Blue circle is the first unit.
Green circle is a tree.

Select the unit:
right click to move.
"c" to open invintory.
Shift + "c" to open and align invintory.
"a" adds a new unit to the player1 controller.


************

Features:
One circle path finding.
2d scrolling and mouse adjustment.
Character selection.
Invintory viewing and adding.

************


Character has a location, and so does camera. Left click and drag to move camera location.
Scrolling in and out.

A method that makes a character. Inits all its variables along with panels for displaying its invintory and stats.


Clicking + Dragging
Legebly write out the order of opperation for mouse interactions.
All mouse interactions go throught the gLoops.

Fix bug when walking to tree.
If player clicked ON tree then go to closest point touching tree.
If player clicked TOO CLOSE to tree then project that point out and go there.

Need to add tree cutting.

If char is following the mouse being held down and then it overlaps a panel it should keep moving to the mouse. But if the mouse is being help down and it overlaps a panel and the char wansnt alreay moving, then dont let the char move unless mouse is held down off the panel.

Y axis scrolling, just isometric.

Adjustable size for panels.

Shift + "c" when selection a unit (as opposed to "c" should open the character menue, back in its normal corner position.

Controller Method. Has all of the units inside of it. Draw relative to that controller.

Draging over multiple characters.

Drawing everything relative controller and its camera loc.
Controller needs to get information of every other entity sent to it to be drawn.
I shouldnt plug in the whole controller just the information about the entities.

I should make a list of selected units instead of having it be relative to the unit. Because i want the controller to be able to select units of other controllers. Not to be able to move them, just select and inspect and such.
For now you can only select your units.

Screen dragging.







