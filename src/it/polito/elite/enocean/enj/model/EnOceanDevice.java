/*
 * EnJ - EnOcean Java API
 * 
 * Copyright 2014 Andrea Biasi, Dario Bonino 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package it.polito.elite.enocean.enj.model;

import it.polito.elite.enocean.enj.EEP26.EEP;
import it.polito.elite.enocean.enj.EEP26.EEPIdentifier;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Hashtable;

/**
 * A class representing EnOcean devices, it allows easier composition of
 * device-related packets and easier deconding / encoding of device-specific
 * information. It supports typically one (but many are permitted) one EnOcean
 * Equipment Profile which defines the device functions and capabilities.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 * @authr <a href="mailto:biasiandrea04@gmail.com">Andrea Biasi </a>
 * 
 * @param <T>
 *            The EnOcean Equipment Profile supported by the device
 * 
 */
public class EnOceanDevice<T extends EEP> implements Serializable
{

	/**
	 * The class version, to be used in serialization / de-serialization
	 */
	private static final long serialVersionUID = 1L;

	// the set of supported profiles
	private Hashtable<EEPIdentifier, T> supportedProfiles;

	// the device address
	byte[] address = new byte[4];

	// the device manufacturer
	byte[] manufacturerId = new byte[3];

	/**
	 * Creates a new {@link EnOceanDevice} instance representing a physical
	 * device having one or more EnOcean EquipmentProfiles.
	 * 
	 * @param address
	 *            The low level address of the device
	 * @param manufacturerId
	 *            The low level manufacturer id associated to the device
	 */
	public EnOceanDevice(byte[] address, byte[] manufacturerId)
	{
		// store the device address
		this.address = address;

		// store the manufacturer id
		this.manufacturerId = manufacturerId;

		// prepare the EEP set
		this.supportedProfiles = new Hashtable<>();
	}

	/**
	 * Provides back the low level address for this device.
	 * 
	 * @return the address as a byte array.
	 */
	public byte[] getAddress()
	{
		return address;
	}

	/**
	 * Provides back the low level manufacturer id of this device
	 * 
	 * @return the manufacturerId
	 */
	public byte[] getManufacturerId()
	{
		return manufacturerId;
	}

	/**
	 * Associates this device instance with a newly created instance of EnOcean
	 * Equipment Profile, of the given class.
	 * 
	 * @param eepId
	 *            The {@link EEPIdentifier} of the profile to add
	 * @param eepClass
	 *            The corresponding class, which must extend {@link EEP}
	 */
	public void addSupportedEEP(EEPIdentifier eepId, Class<T> eepClass)
	{
		// store the entry by building the corresponding instances
		try
		{
			// get the profile instance
			T eep = eepClass.newInstance();

			// add the instance to the set of supported instances
			this.supportedProfiles.put(eepId, eep);

		}
		catch (InstantiationException | IllegalAccessException e)
		{
			// TODO: add a logging system here
			e.printStackTrace();
		}
	}

	/**
	 * Get the <code>&lt;T extends {@link EEP}&gt;</code> instance associated to
	 * this device, given the corresponding EEP identifier.
	 * 
	 * @param eepId
	 *            The {@link EEPIdentifier} of the
	 *            <code>&lt;T extends {@link EEP}&gt;</code> to retrieve.
	 * @return the <code>&lt;T extends {@link EEP}&gt;</code> instance, if
	 *         present, or null otherwise.
	 */
	public T getSupportedEEP(EEPIdentifier eepId)
	{
		return this.supportedProfiles.get(eepId);
	}

	/**
	 * Provides back the device address as an integer. The returned id is meant
	 * to be used for "detecting" the same device at the application level.
	 * Currently no backward translation to low level address is supported.
	 * 
	 * @return The device address as an integer number.
	 */
	public int getDeviceUID()
	{
		return ByteBuffer.wrap(this.address).getInt();
	}

	/**
	 * Provides back the device manufacturer id as an integer. The returned id
	 * is meant to be used as manufacturer identifier. Currently, no back
	 * translation is supported.
	 * 
	 * @return The manufacturer id as an integer.
	 */
	public int getManufacturerUID()
	{
		return ByteBuffer.wrap(this.manufacturerId).getInt();
	}
}