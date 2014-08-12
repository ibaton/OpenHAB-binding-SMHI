package org.openhab.binding.smhi.internal;

import org.openhab.binding.smhi.SmhiBindingProvider;
import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;
import org.openhab.core.library.items.NumberItem;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;


/**
 * This class is responsible for parsing the binding configuration.
 * 
 * @author Mattias Markehed
 */
public class SmhiGenericBindingProvider extends AbstractGenericBindingProvider implements SmhiBindingProvider {
	
	/**
	 * {@inheritDoc}
	 */
	public String getBindingType() {
		return "smhi";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateItemType(Item item, String bindingConfig) throws BindingConfigParseException {
		if (!(item instanceof NumberItem)) {
			throw new BindingConfigParseException("item '" + item.getName()
					+ "' is of type '" + item.getClass().getSimpleName()
					+ "', only Number are allowed - please check your *.items configuration");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processBindingConfiguration(String context, Item item, String bindingConfig) throws BindingConfigParseException {
		
		super.processBindingConfiguration(context, item, bindingConfig);
				
		String[] configParts = bindingConfig.trim().split(":");
		if (configParts.length != 3) {
			throw new BindingConfigParseException("Smhi binding configuration must contain three parts");
		}
		
		SmhiBindingConfig config = new SmhiBindingConfig();
		
		config.latitude = Float.valueOf(configParts[0]);
		config.longitude = Float.valueOf(configParts[1]);
		config.parameter = String.valueOf(configParts[2]).toLowerCase();
		
		addBindingConfig(item, config);
	}
	
	@Override
	public float getLongitude(String itemName) {
		SmhiBindingConfig config = (SmhiBindingConfig) bindingConfigs.get(itemName);
		return config != null ? config.longitude : 0;
	}

	@Override
	public float getLatitude(String itemName) {
		SmhiBindingConfig config = (SmhiBindingConfig) bindingConfigs.get(itemName);
		return config != null ? config.latitude : 0;
	}
	
	@Override
	public String getParameter(String itemName) {
		SmhiBindingConfig config = (SmhiBindingConfig) bindingConfigs.get(itemName);
		return config != null ? config.parameter : null;
	}
	
	/**
	 * Data structure representing the item configuration. 
	 */
	static private class SmhiBindingConfig implements BindingConfig {
		public float longitude;
		public float latitude;
		
		/** The data to fetch. Valid parameters can be found in {@link org.openhab.binding.smhi.internal.Constants }*/
		public String parameter;
	}
}
