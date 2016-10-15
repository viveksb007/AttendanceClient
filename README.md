# AttendanceClient

[![Build Status](https://travis-ci.org/viveksb007/AttendanceClient.svg)](https://travis-ci.org/viveksb007/AttendanceClient)

#### This project aims to reduce time to take attendance in class. Teacher would open [Server](https://github.com/viveksb007/AttendanceServer) app and click a button which would open Wifi Hotspot of his/her phone available for Clients i.e Students to connect to mark their attendance.
#### Students would have [Clients](https://github.com/viveksb007/AttendanceClient) app on their phone which when used would mark attendance with a conformation message.

Problems Faced and Solutions :
* Proxy Prevention : Uses Clients MAC Address to identify each device. So until you  root your phone and change your MAC Address during attendance time, you can`t mark false attendance.
* Connection Limit of WiFi Hotspot : As soon as Clients app receives conformation of attendance, its WiFi is disabled. So others could mark their attendance.

# License
>MIT License

>Copyright (c) 2016 Vivek Singh Bhadauria

>Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

>The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.




