OpenHAB-binding-SMHI
====================

A SMHI weather binding for OpenHab. 

For installation of the binding, please see [Wiki](https://github.com/openhab/openhab/wiki/Bindings).

##Generic Item Binding Configuration

In order to bind an item to a SMHI requst, you need to add some binding information in your item file.
The syntax of the configuration is listed bellow:

smhi="latitude:longitude:parameter"

Latitude and latitude for your location can be found using [bing](http://www.bing.com/maps). 

Valid parameters:
* *temperature* - Temperature. C.
* *probability_thunderstorm* - Probability of thunderstorm. %.
* *pressure* - Air pressure. hPa.
* *visibility* - Visibility. km.
* *wind_direction* - Wind direction. Degrees.
* *wind_velocity* - Wind velocity. m/s.
* *gust* - Wind gust. m/s
* *humidity* - Relative humidity. %.
* *clouds* - Cloud coverage. 0-9.
* *precipitation* - Precipitation intensity. mm/h.

Some example: \n
```Number Temperature "Temperature [%.1f C°]" { smhi="57.683289:12.008490:temperature" }
Number Humidity "Humidity [%.1f %%]" { smhi="57.683289:12.008490:visibility" }
Number Thunderstorm "Thunderstorm [%.1f %%]" { smhi="57.683289:12.008490:probability_thunderstorm" }```
