/*
 * Copyright 2014 Red Hat, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.mapping;

import de.braintags.io.vertx.pojomapper.IDataStore;

/**
 * Defines how the structural process of writing and reading of a field is performed. Technically there are existing 3
 * relevant IPropertyMapper:<br/>
 * <UL>
 * <LI/>pure property: the content of the given field is transformed from / into the needed format and written directly
 * as content of the {@link IStoreObject} or the mapper
 * <LI/>referenced: the content of the field is written / fetched somewehere else ( another table / column etc. ). When
 * writing into the {@link IDataStore}, this property is replaced by an identifyer. When reading, the object is loaded
 * and created from the other position again
 * <LI>embedded: the content of the field is written / read completely into / from the field as a substructure.
 * </UL>
 * 
 * @author Michael Remme
 * 
 */

public interface IPropertyMapper {

  /**
   * place the content of the given {@link IField} into the {@link IStoreObject}
   * 
   * @param mapper
   *          the mapper object to be handled
   * @param storeObject
   *          the instance of {@link IStoreObject} where the content shall be placed
   * @param field
   *          the {@link IField} to be handled
   */
  void intoStoreObject(Object mapper, IStoreObject storeObject, IField field);

  /**
   * fetch the content from the {@link IStoreObject} and store it inside the mapper
   * 
   * @param mapper
   *          the mapper to be handled
   * @param storeObject
   *          the instance of {@link IStoreObject}, where the content shall be fetched from
   * @param field
   *          the {@link IField} to be handled
   */
  void fromStoreObject(Object mapper, IStoreObject storeObject, IField field);

}
