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

import java.util.Map;
import java.util.Set;

import com.att.detsplatform.cosc.api.COSCInterface;
import com.att.detsplatform.cosc.base.COSCEntry;
import com.att.detsplatform.cosc.base.COSCKey;
import com.att.detsplatform.cosc.base.exceptions.COSCException;
import com.att.detsplatform.cosc.base.exceptions.InvalidEntryException;
import com.att.detsplatform.cosc.base.exceptions.TypeNotFoundException;

/**
 * The Interface COSCService.
 * 
 */
public interface COSCService {

	/** The version. */
	String VERSION = "v1";

	/**
	 * Gets the cache service.
	 *
	 * @return the cache service
	 */
	COSCInterface<COSCKey> getCacheService();

	/**
	 * Creates the COSC key.
	 *
	 * @param pkey
	 *            the pkey
	 * @param keyName
	 *            the key name
	 * @return the COSC key
	 */
	COSCKey createCOSCKey(String pkey, String keyName);

	/**
	 * Creates the COSC entry.
	 *
	 * @param keyname
	 *            the keyname
	 * @param obj
	 *            the obj
	 * @return the COSC entry
	 */
	COSCEntry createCOSCEntry(String keyname, Object obj);

	/**
	 * Creates the COSC entry.
	 *
	 * @param keyname
	 *            the keyname
	 * @param version
	 *            the version
	 * @param obj
	 *            the obj
	 * @return the COSC entry
	 */
	COSCEntry createCOSCEntry(String keyname, String version, Object obj);

	/**
	 * Gets the.
	 *
	 * @param key
	 *            the key
	 * @return the COSC entry
	 * @throws COSCException
	 *             the COSC exception
	 */
	COSCEntry get(COSCKey key) throws COSCException;

	/**
	 * Put.
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @throws COSCException
	 *             the COSC exception
	 */
	void put(COSCKey key, COSCEntry value) throws COSCException;

	/**
	 * Delete.
	 *
	 * @param key
	 *            the key
	 * @throws COSCException
	 *             the COSC exception
	 */
	void delete(COSCKey key) throws COSCException;

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
	Map<COSCKey, COSCEntry> getAll(Set<COSCKey> paramSet) throws TypeNotFoundException, COSCException;

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
	void putAll(Map<COSCKey, COSCEntry> paramMap) throws TypeNotFoundException, InvalidEntryException, COSCException;

	/**
	 * Delete all.
	 *
	 * @param paramSet
	 *            the param set
	 * @throws COSCException
	 *             the COSC exception
	 */
	void deleteAll(Set<COSCKey> paramSet) throws COSCException;

}
