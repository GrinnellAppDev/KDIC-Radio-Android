# KDIC Android


*__UPDATED:__ 20 January, 2016*

## KDIC Streaming App

The KDIC Streaming App is a convenient way for anyone to tune into their
favorite radio station, KDIC, based out of Grinnell College, IA.

Source for Grinnell-Menu can be obtained [here](https://github.com/GrinnellAppDev/KDIC-Radio-Android)!

You can obtain the source with the following command:
```shell
git clone git://github.com/GrinnellAppDev/KDIC-Radio-Android.git
```

If you would like to contribute, shoot us a pull request.  


## Authors and Contributors

Homebrewed by [Grinnell AppDev](http://appdev.grinnell.edu/)!

### Current Builds

- [Prabir Pradhan](https://github.com/prabirmsp)

### Previous Builds

- [Michael Owusu](https://github.com/mkowusu)
- Patrick Triest
- Spencer Liberto
- Travis Law

## Screenshots

![](screenshots/1.png)

![](screenshots/2.png)

![](screenshots/3.png)

![](screenshots/4.png)

![](screenshots/5.png)

## Organization

### Directory Structure

Generated with `tree -d -I build`.
```
.
├── app
│   └── src
│       └── main
│           ├── java
│           │   └── edu
│           │       └── grinnell
│           │           └── kdic
│           │               ├── schedule
│           │               └── visualizer
│           └── res
│               ├── anim
│               ├── drawable
│               ├── drawable-hdpi
│               ├── drawable-mdpi
│               ├── drawable-xhdpi
│               ├── drawable-xxhdpi
│               ├── drawable-xxxhdpi
│               ├── layout
│               ├── menu
│               ├── values
│               └── values-w820dp
├── audiovisualizer
│   ├── libs
│   └── src
│       └── main
│           ├── java
│           │   └── edu
│           │       └── grinnell
│           │           └── audiovisualizer
│           └── res
│               ├── drawable
│               └── values
├── gradle
│   └── wrapper
└── screenshots


```
- `KDIC-android.php` is the PHP script used to parse the HTML table schedule data to JSON, and is hosted [here]().


## License
```
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
```
