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
* figure out if hardwaremap H is needed at all; if not, remove it

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
* in `update(KeyEvent)`, change to switch statment
* simplify the to/from byte array processes
* rename `atRest` to `isAtRest`
* simplify `toString()` button compilation

#####hardware.HardwareMap

* rearrange members
* Make fields private; add getters/setters
* change the DeviceMapping generic to <T>

#####hardware.IrSeekerSensor

* rearrange members

#####hardware.Servo

* rearrange members
* Separate `Direction` into its own thing because the same thing is used in `DcMotor`
* rename min/max constants and add 'default' to differentiate between the two sets of constants.

#####hardware.ServoController

* rearrange members
* remove `abstract` modifier from methods.

#####hareware.configuration.ControllerConfiguration

* declare serial version id
* remove the private field?
* remove the `super` from the call to "setname()" in the constructor
* remove the `getType` method as it is the same at its supertype
* rename the badly-named `addDevices` method, because it really sets the devices.
* change `deviceTypeToConfigType()` and `configTypeToDeviceType` to use a switch

#####hareware.configuration.DeviceConfiguration

* declare serial version id
* remove redundant `implements Serializable`
* move `disabled` boolean here from subclasses

#####hareware.configuration.DeviceInfoAdapter

* remove the redundant implementation of `ListAdapter`

#####hareware.configuration.LegacyModuleControllerConfiguration

* declare serial version id

#####hareware.configuration.MotorConfiguration

* declare serial version id
* remove redundant `super` calls
* add a constructor that accepts a `ConfigurationType` or assumes one.

#####hareware.configuration.MotorControllerConfiguration

* declare serial version id
* remove redundant `implements Serializable`

#####hardware.configuration.ReadXMLFileHandler

* lines 106 and 160: change declaration from `ArrayList<>` to `List<>`
* compress lines 112-115 into `tagname != null` on 116; do the same thing with the servo controller and motor controller functions
* condense `handleLegacyModule()`, `handleMotorController()`, and `handleServoController()` to one function

#####hardware.configuration.ServoConfiguration

* remove unecessary `super` calls

#####hareware.configuration.ServoControllerConfiguration

* declare serial version id

#####hardware.configuration.Utility

* lines 94-95: replace both messages with a single String
* make `createLists()` accept a `Map<>` instead of a `Set<Map.Entry<>>`

#####hardware.configuration.WriteXMLFileHandler

* get rid of the `indentation` String array.

#####hardware.mock.MockDeviceManager

* figure out if the `SerialNumber` is needed for some overriden methods

#####hardware.mock.MockHardwareFactory

* turn the construction assignment into a call to `setHardwareMap`

#####hardware.mock.MockUsbDcMotorController

* find out why 135 and 136 are so special on lines 114, 119
* return `null` instead of a `new` on 139 ????

#####robocol.Command

* find out if `CHARSET` is used for anything
* rename members: remove all the M's
* replace `equals()` body with `compareTo() == 0`
* do something with the `generateTimestamp()` method

#####robocol.Heartbeat
