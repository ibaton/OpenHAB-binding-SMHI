package org.openhab.binding.smhi.internal;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openhab.binding.smhi.SmhiBindingProvider;
import org.openhab.core.binding.AbstractActiveBinding;
import org.openhab.core.library.types.DecimalType;
import org.openhab.io.net.http.HttpUtil;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;


/**
 * The SMHI binding Refresh Service polls weather data from the SMHI servers 
 * with a fixed interval and posts a new event of type ({@link DecimalType} to the event bus.
 * The interval is 10 minutes. 
 * 
 * @author Mattias Markehed
 */
public class SmhiBinding extends AbstractActiveBinding<SmhiBindingProvider> implements ManagedService {

	private static final Logger logger = LoggerFactory.getLogger(SmhiBinding.class);

	/** Keeps track of the last time item was updated. */
	private Map<String, Long> lastUpdateMap = new HashMap<String, Long>();
	
	/** The server used to store the SMHI weather data. */
	protected static final String hostname = "http://opendata-download-metfcst.smhi.se/api/category/pmp1.5g/version/1/geopoint/lat/%s/lon/%s/data.json";
	
	/** Update with 10 minutes interval. */
	private static final long refreshInterval = 600000L;
	
	/** Timeout for weather data requests. */
	private static final int SMHI_TIMEOUT = 5000;
	
	@Override
	protected String getName() {
		return "SMHI Refresh Service";
	}
	
	@Override
	protected long getRefreshInterval() {
		return refreshInterval;
	}
		
	/**
	 * @{inheritDoc}
	 */
	@Override
	public void execute() {
		
		if (!bindingsExist()) {
			logger.info("There is no existing SMHI binding configuration => refresh cycle aborted!");
			return;
		}

		for (SmhiBindingProvider provider : providers) {
			for (String itemName : provider.getItemNames()) {
				
				Long lastUpdateTimeStamp = lastUpdateMap.get(itemName);
				if (lastUpdateTimeStamp == null) {
					lastUpdateTimeStamp = 0L;
				}
				
				long age = System.currentTimeMillis() - lastUpdateTimeStamp;
				boolean needsUpdate = age >= refreshInterval;
				
				if (needsUpdate) {
					float longitude = provider.getLongitude(itemName);
					float latitude = provider.getLatitude(itemName);
					String parameter = provider.getParameter(itemName);
					String url = String.format(hostname, latitude, longitude);
										
					String response = HttpUtil.executeUrl("GET", url, null, null, "application/json", SMHI_TIMEOUT);
		
					Gson gson = new GsonBuilder().create();
					SmhiDataList dataList = gson.fromJson(response, SmhiDataList.class);
					if(dataList != null){
						float value = -1;
						if(parameter.equals(Constants.PARAMETER_TEMPERATURE)){
							value = dataList.timeseries.get(0).temperature;
						} else if(parameter.equals(Constants.PARAMETER_THUNDERSTORM)){
							value = dataList.timeseries.get(0).probabilityThunderstorm;
						} else if(parameter.equals(Constants.PARAMETER_CLOUDS)){
							value = dataList.timeseries.get(0).cloudCoverage;
						}else if(parameter.equals(Constants.PARAMETER_HUMIDITY)){
							value = dataList.timeseries.get(0).humidity;
						}else if(parameter.equals(Constants.PARAMETER_PRECIPITATION)){
							value = dataList.timeseries.get(0).totalPrecipitation;
						}else if(parameter.equals(Constants.PARAMETER_PRESSURE)){
							value = dataList.timeseries.get(0).airPressure;
						}else if(parameter.equals(Constants.PARAMETER_VISIBILITY)){
							value = dataList.timeseries.get(0).visibility;
						}else if(parameter.equals(Constants.PARAMETER_WIND_DIRECTION)){
							value = dataList.timeseries.get(0).windDirection;
						}else if(parameter.equals(Constants.PARAMETER_WIND_GUST)){
							value = dataList.timeseries.get(0).windGust;
						}else if(parameter.equals(Constants.PARAMETER_WIND_VELOCITY)){
							value = dataList.timeseries.get(0).windSpeed;
						}
						
						if(value != -1){
							eventPublisher.postUpdate(itemName, new DecimalType(value));
						}
					}
					lastUpdateMap.put(itemName, System.currentTimeMillis());
				}
			}
		}
		
	}
		
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("rawtypes")
	public void updated(Dictionary config) throws ConfigurationException {
		// No configuration is used
		setProperlyConfigured(true);
	}
	
	class SmhiDataList {
		
		@SerializedName("lat")
		private float latitude;
		
		@SerializedName("lon")
		private float longitude;
		
		@SerializedName("referenceTime")
		private String time;
		
		@SerializedName("timeseries")
		public List<WeatherData> timeseries;
	}
	
	class WeatherData {
		
		/** Air pressure in hPa. */
		@SerializedName("msi")
		private float airPressure;
		
		/** Temperature in Celsius. */
		@SerializedName("t")
		private float temperature;
		
		/** Visibility in kilometers. */
		@SerializedName("vis")
		private float visibility;
		
		/** Wind direction in degrees. */
		@SerializedName("wd")
		private float windDirection;
		
		/** Wind speed in m/s. */
		@SerializedName("ws")
		private float windSpeed;
		
		/** Wind gust in m/s. */
		@SerializedName("gust")
		private float windGust;
		
		/** Humidity in percentage */
		@SerializedName("r")
		private float humidity;
		
		/** Probability of thunder in percentage. */
		@SerializedName("tstm")
		private float probabilityThunderstorm;
		
		/** Cloud coverage, value between 0-8. */
		@SerializedName("tcc")
		private float cloudCoverage;
		
		/** Total precipitation in mm/h. */
		@SerializedName("pit")
		private float totalPrecipitation;
		
		/** Precipitation category in. */
		@SerializedName("pcat")
		private float precipitationCategory;
	}
}
