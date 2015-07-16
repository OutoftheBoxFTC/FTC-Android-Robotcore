# TO-DO List

#####eventloop.EventLoop:

* remove `abstract` label from interface methods
* change doc formatting ?

#####eventloop.EventLoopManager

* reorder all members
* wherever possible, split method into smaller methods
* create a sendError method: buildAndSendTelemetry(SYS_telem,  robotlog.globalerror)
* create a logDebug(String) method that checks if DEBUG, then logs the message.

#####eventloop.opmode.OpMode

* make members private

#####eventloop.opmode.OpModeManager

* condense the two maps into one
* Determine if a different process happens for objects in the differnt maps
* change 'register(String, Class<?>) to accept (String, Class<? extends OpMode>)
* figure out the unknonn variables/methods
* figure out if hardwaremap H is needed at all

#####factory.RobotFactory

* consider making `Robot` handle all its own initialization

#####hardware.AccelerationSensor

* rearrange members

#####hardware.CompassSensor

* rearrange memberss

#####hardware.DcMotor

* rearrange members
* find out exactly what setPowerFloat does

#####hardware.DcMotorController

* remove `abstract` labels
* find an implementation

#####hardware.DeviceManager

* find an implementation
* move 'createUsbDcMotorController', 'createDcMotor', etc. to their respective constructors.
* rearrange members

#####hardware.DigitalChannelController

* rearrange members

#####hardware.Gamepad

* make members private; add getters/ setter
* find out where the members are being assigned, and move that functionality to inside `Gamepad`.
* expand out the fully qualified type names from the method parameters

