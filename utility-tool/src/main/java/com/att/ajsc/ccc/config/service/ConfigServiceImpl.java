/*******************************************************************************
 *   BSD License
 *    
 *   Copyright (c) 2017, AT&T Intellectual Property.  All other rights reserved.
 *    
 *   Redistribution and use in source and binary forms, with or without modification, are permitted
 *   provided that the following conditions are met:
 *    
 *   1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *      and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *      conditions and the following disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. All advertising materials mentioning features or use of this software must display the
 *      following acknowledgement:  This product includes software developed by the AT&T.
 *   4. Neither the name of AT&T nor the names of its contributors may be used to endorse or
 *      promote products derived from this software without specific prior written permission.
 *    
 *   THIS SOFTWARE IS PROVIDED BY AT&T INTELLECTUAL PROPERTY ''AS IS'' AND ANY EXPRESS OR
 *   IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *   MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *   SHALL AT&T INTELLECTUAL PROPERTY BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *   SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;  LOSS OF USE, DATA, OR PROFITS;
 *   OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *   CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *   ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *   DAMAGE.
 *******************************************************************************/
package com.att.ajsc.ccc.config.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.ajsc.ccc.config.common.CCCJsonParser;
import com.att.ajsc.ccc.config.model.ConfigValue;
import com.att.ajsc.ccc.config.service.cosc.COSCService;
import com.att.ajsc.ccc.config.service.cosc.COSCServiceImpl;
import com.att.ajsc.common.Tracable;
import com.att.ajsc.logging.AjscEelfManager;
import com.att.detsplatform.cosc.api.COSCInterface;
import com.att.detsplatform.cosc.base.COSCEntry;
import com.att.detsplatform.cosc.base.COSCKey;
import com.att.detsplatform.cosc.base.exceptions.COSCException;
import com.att.eelf.configuration.EELFLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;


@Service
public class ConfigServiceImpl implements ConfigService {

	private static EELFLogger log = AjscEelfManager.getInstance().getLogger(ConfigServiceImpl.class);

	@Autowired
	private static COSCInterface<COSCKey> cache ;


    public ConfigServiceImpl(){

	}

	private static final COSCService coscService = new COSCServiceImpl("salesApplicationId");


    @Override
	public ConfigValue setConfigValue(ConfigValue configValue){

    	log.info(configValue.getCategory());
		List<ConfigValue> configValues = new ArrayList<ConfigValue>();
		//configValues.add(configValue);
		String rootKeyName = "sales_checkout.terms_and_conditions";
		String rootId = "12345678";

		try {
			COSCKey coscKey = coscService.createCOSCKey(rootId, rootKeyName);
			COSCEntry coscEntry = coscService.get(coscKey);
			if (Optional.ofNullable(coscEntry).isPresent()) {
				log.info("Data Present " + coscEntry.getName());
				configValues = CCCJsonParser.getObjectFromJson(coscEntry.getValue(), List.class);
				configValues.add(configValue);
				coscEntry = coscService.createCOSCEntry(rootKeyName, configValues);
				coscService.put(coscKey, coscEntry);
			} else {
				//log.info("Data Not Present  " + coscEntry.getName());
				configValues.add(configValue);
				coscEntry = coscService.createCOSCEntry(rootKeyName, configValues);
				coscService.put(coscKey, coscEntry);
			}
		}
		catch(COSCException e){
			e.printStackTrace();
		}

    	return configValue;
	}


	@Override
	@Tracable(message="invoking Configure values ")
	public List<ConfigValue> getConfigValues() {
//		configValue = getConfigValueDetail();
		String rootKeyName = "sales_checkout.terms_and_conditions";
		String rootId = "12345678";
		List<ConfigValue> configValue = null;
	try{

			COSCKey coscKey = coscService.createCOSCKey(rootId, rootKeyName);
			COSCEntry coscEntry = coscService.get(coscKey);
			if (Optional.ofNullable(coscEntry).isPresent()) {
				log.info("Data Present " + coscEntry.getName());
				configValue = CCCJsonParser.getObjectFromJson(coscEntry.getValue(), List.class);
				 
			}
			else	{
				log.info("Data Not Present  " + coscEntry.getName());
			}
		} catch (COSCException cosce) {
			cosce.printStackTrace();
		}
		return configValue;
	}


	public ConfigValue getConfigValueDetail() {

		log.debug("testCOSCConnectivity method start");
		
		String keyName = "sales_profile.customer_info";

		String response = null;
		String uuid="";
		String data = "{ \"version\" : 0, \"profile_type\" : \"authuser\", \"wireline_customer_type\" : \"Consumer\", \"browser_id\" : \"A001370377552\", \"pvt_profile\" : false, \"locale\" : \"en_US\" }";
		try {

			COSCKey coscKey = coscService.createCOSCKey(uuid, keyName);
			COSCEntry coscEntry = coscService.get(coscKey);
			if (Optional.ofNullable(coscEntry).isPresent()) {
				System.out.println("Inside [testCOSCConnectivity]:" + " value of coscEntry"
						+ coscEntry.getValue() + " value of coscKey" + coscKey.getObjectKey());
				response = coscEntry.getValue().toString();
			}
			else	{
				System.out.println("Data not found before - Saving data into COSC");
				coscEntry = coscService.createCOSCEntry(keyName, data);
				coscService.put(coscKey,coscEntry);

			}
			System.out.println("testCOSCConnectivity end point call completed");

		} catch (COSCException cosce) {
			cosce.printStackTrace();

		}
		log.debug("testCOSCConnectivity method End");
		return new ConfigValue();
	}

}