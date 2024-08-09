# Update Headers Burp Extension
This Burp extension lets you update your headers inside, for example, the Repeater to newer headers. The main scenario for this is replacing the cookies inside an old Repeater tab with the latest cookies. The replacement headers will be selected based on the newest request with the same `Host` header as the selected request.

## Build
```sh
make
# or 
docker run --rm -u $(id -u):$(id -g) -v $(pwd):/home/gradle gradle:8.7.0-jdk17-alpine gradle build
./build/libs/burp-update-headers-1.0.jar .
```