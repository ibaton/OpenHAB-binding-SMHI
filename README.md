OpenHAB-binding-SMHI
====================

A SMHI weather binding for OpenHab. 

For installation of the binding, please see [Wiki](https://github.com/openhab/openhab/wiki/Bindings).

##Download
[org.openhab.binding.smhi-1.6.0.jar](https://drive.google.com/file/d/0B2sLEbuzoWR4aG5POGFzX1lJOGM/edit?usp=sharing)

##Generic Item Binding Configuration

In order to bind an item to a SMHI requst, you need to add some binding information in your item file.
The syntax of the configuration is listed bellow:

smhi="latitude:longitude:parameter"

Latitude and latitude for your location can be found using [bing](http://www.bing.com/maps).
Latitude must be between 52.50 and 70.75.
Longitude must be betweem 2.25 and 38.00. 

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
