/* <AT&T Copyright>
 * Copyright (C) 2017 AT&T
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from AT&T.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * AT&T MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. AT&T
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * </AT&T Copyright>
 */
package com.att.ajsc.ccc.config.service.cosc;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.Map;
import java.util.Set;

import com.att.ajsc.ccc.config.service.cosc.COSCService;
import com.att.detsplatform.cosc.api.COSCInterface;
import com.att.detsplatform.cosc.api.impl.ServiceFactory;
import com.att.detsplatform.cosc.base.COSCEntry;
import com.att.detsplatform.cosc.base.COSCKey;
import com.att.detsplatform.cosc.base.exceptions.COSCException;
import com.att.detsplatform.cosc.base.exceptions.InvalidEntryException;
import com.att.detsplatform.cosc.base.exceptions.TypeNotFoundException;

/**
 * COSC connect implementation class.
 *
 */
public class COSCServiceImpl implements COSCService {
	/** The application id. */
	private String applicationId = null;

	/** COSC cache instance declaration. */
	private COSCInterface<COSCKey> cache;

	/**
	 * private constructor *.
	 *
	 * @param applicationId
	 *            the application id
	 */
	public COSCServiceImpl(String applicationId) {
		this.applicationId = applicationId;
	}

	/**
	 * New instance.
	 *
	 * @param applicationId
	 *            the application id
	 * @return the COSC service
	 */
	public COSCService newInstance(String applicationId) {
		return new COSCServiceImpl(applicationId);
	}

	/**
	 * cache is set from ServiceFactory.
	 *
	 * @return COSCInterface
	 */
	@Override
	public COSCInterface<COSCKey> getCacheService() {
		if (cache == null) {
			cache = ServiceFactory.getInstance().getCacheService();
		}
		return cache;
	}

	/**
	 * Creates the COSCKey Object.
	 *
	 * @param pkey
	 *            the pkey
	 * @param keyName
	 *            the key name
	 * @return coscKey
	 */
	@Override
	public COSCKey createCOSCKey(final String pkey, final String keyName) {
		return new COSCKey(pkey, keyName.toString(), applicationId);

	}

	/**
	 * Create COSC Entry object for put() operation. It converts Domain object
	 * to JSON String.
	 *
	 * @param keyName
	 *            the key name
	 * @param obj
	 *            the obj
	 * @return - COSCEntry Object
	 */
	@Override
	public COSCEntry createCOSCEntry(final String keyName, final Object obj) {

		return createCOSCEntry(keyName, null, obj);
	}

	/**
	 * Create COSC Entry object for put() operation. It converts Domain object
	 * to JSON String.
	 *
	 * @param keyName
	 *            the key name
	 * @param version
	 *            the version
	 * @param obj
	 *            the obj
	 * @return - COSCEntry Object
	 */
	@Override
	public COSCEntry createCOSCEntry(final String keyName, final String version, final Object obj) {

		final Object domainJSONValue = JsonService.getJsonFromObject(obj);
		final COSCEntry coscEntry = new COSCEntry();
		coscEntry.setName(keyName.toString());
		if (isEmpty(version)) {
			coscEntry.setVersion(VERSION);
		} else {
			coscEntry.setVersion(version);
		}
		coscEntry.setValue(domainJSONValue);

		return coscEntry;
	}

	/**
	 * Invokes COSI API get() operation based on COSCKey.
	 *
	 * @param key
	 *            the key
	 * @return the COSC entry
	 * @throws COSCException
	 *             the COSC exception
	 */
	@Override
	public COSCEntry get(final COSCKey key) throws COSCException {
		return getCacheService().get(key);
	}

	/**
	 * This method accepts COSCKey and Entry object and invokes COSC put() API.
	 *
	 * @param key
	 *            the key
	 * @param entry
	 *            the entry
	 * @throws COSCException
	 *             the COSC exception
	 */
	@Override
	public void put(final COSCKey key, final COSCEntry entry) throws COSCException {
		if (isValidCOSCKey(key) && isValidCOSCEntry(entry)) {
			getCacheService().put(key, entry);
		}
	}

	/**
	 * This method invokes COSC delete API. Based on the COSC Key.
	 *
	 * @param key
	 *            the key
	 * @throws COSCException
	 *             the COSC exception
	 */
	@Override
	public void delete(final COSCKey key) throws COSCException {
		if (isValidCOSCKey(key)) {
			getCacheService().delete(key);
		}
	}

	/**
	 * Gets the all.
	 *
	 * @param paramSet
	 *            the param set
	 * @return the all
	 * @throws TypeNotFoundException
	 *             the type not found exception
	 * @throws COSCException
	 *             the COSC exception
	 */
	@Override
	public Map<COSCKey, COSCEntry> getAll(final Set<COSCKey> paramSet) throws TypeNotFoundException, COSCException {
		return getCacheService().getAll(paramSet);
	}

	/**
	 * Put all.
	 *
	 * @param paramMap
	 *            the param map
	 * @throws TypeNotFoundException
	 *             the type not found exception
	 * @throws InvalidEntryException
	 *             the invalid entry exception
	 * @throws COSCException
	 *             the COSC exception
	 */
	@Override
	public void putAll(final Map<COSCKey, COSCEntry> paramMap)
			throws TypeNotFoundException, InvalidEntryException, COSCException {
		getCacheService().putAll(paramMap);
	}

	/**
	 * Delete all.
	 *
	 * @param paramSet
	 *            the param set
	 * @throws COSCException
	 *             the COSC exception
	 */
	@Override
	public void deleteAll(final Set<COSCKey> paramSet) throws COSCException {

		getCacheService().deleteAll(paramSet);
	}

	/**
	 * Returns true if COSCKey contains valid parameters.
	 *
	 * @param key
	 *            the key
	 * @return true, if is valid COSC key
	 */
	private boolean isValidCOSCKey(final COSCKey key) {

		boolean isValidCOSCKey = false;

		if (key != null && isNotBlank(key.getObjectKey()) && isNotBlank(key.getPrimaryKey())) {

			System.out.println(
					"COSCCacheServiceImpl: COSC Key: Okey: " + key.getObjectKey() + "-- pKey: " + key.getPrimaryKey());
			key.setApplicationId(applicationId);
			isValidCOSCKey = true;
		} else {
			if (key == null) {
				System.out.println("COSC operation is invoked without valid parameters. COSC key object is NULL.");
			} else {
				System.out.println("COSC operation is invoked without valid parameters. Pkey: " + key.getPrimaryKey()
						+ "-Okey:" + key.getObjectKey());
			}
		}
		return isValidCOSCKey;
	}

	/**
	 * Returns true if COSCEntry contains valid parameters.
	 *
	 * @param entry
	 *            the entry
	 * @return true, if is valid COSC entry
	 */
	private boolean isValidCOSCEntry(final COSCEntry entry) {

		boolean isValidCOSCEntry = false;

		if (entry != null && isNotBlank((String) entry.getValue()) && isNotBlank(entry.getName())) {
			final int length = ((String) entry.getValue()).length();
			System.out.println("COSCCacheServiceImpl: COSC Entry: Schema Name: " + entry.getName() + "-- Value JSON Length: "
						+ length);
			System.out.println("COSCCacheServiceImpl: COSC Entry Object: " + entry.getValue());
			isValidCOSCEntry = true;
		} else {
				if (entry == null) {
					System.out.println("COSC operation is invoked without valid parameters. COSC Entry object is NULL.");
				} else {
					System.out.println("COSC operation is invoked without valid parameters. Schema Name: " + entry.getName());
					if (entry.getValue() != null) {
						System.out.println("COSC operation is invoked without valid parameters. Enry Value Length: "
								+ ((String) entry.getValue()).length());
					}
				}
		}
		return isValidCOSCEntry;
	}

}