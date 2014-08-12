package org.openhab.binding.smhi;

import org.openhab.core.binding.BindingProvider;

/**
 * @author Mattias Markehed
 */
public interface SmhiBindingProvider extends BindingProvider {
		
	public float getLongitude(String itemName);
	
	public float getLatitude(String itemName);
	
	public String getParameter(String itemName);
}
