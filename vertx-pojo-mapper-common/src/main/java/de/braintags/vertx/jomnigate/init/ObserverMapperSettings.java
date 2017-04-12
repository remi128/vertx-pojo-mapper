/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.init;

import java.lang.annotation.Annotation;

import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.mapping.IMapper;

/**
 * The part of {@link ObserverSettings} which defines, on which {@link IMapper} the ObserverSettings shall be executed.
 * The property classDefinition and annotation contain the definition. Examples for a class definition are:
 * <UL>
 * <LI>my.mapper.class<br/>
 * the parent ObserverSettings will be applied for a mapper with the class my.mapper.class
 * <LI>instanceof my.mapper.class<br/>
 * the parent ObserverSettings will be applied for all classes, which are instanceof my.mapper.class
 * </UL>
 * 
 * @author Michael Remme
 * 
 */
public class ObserverMapperSettings {
  private String classDefinition;
  private boolean instanceOf = false;
  private Class<?> mapperClass;
  private Class<? extends Annotation> annotation;

  @SuppressWarnings("unused")
  private ObserverMapperSettings() {
    // only usable for serialization
  }

  /**
   * Constructor for a new instance. The class definition can be something like "my.mapper.class" or "instanceof
   * my.mapper.class"
   * 
   * @param classDefinition
   */
  public ObserverMapperSettings(String classDefinition) {
    setClassDefinition(classDefinition);
  }

  /**
   * Constructor for a new instance, which shall be executed on mapper classes, which are annotated with the given
   * annotation
   * 
   * @param annotation
   */
  public ObserverMapperSettings(Class<? extends Annotation> annotation) {
    setAnnotation(annotation);
  }

  private void init() {
    if (classDefinition != null) {
      String clsName = classDefinition;
      try {
        mapperClass = Class.forName(clsName);
      } catch (ClassNotFoundException e) {
        throw new MappingException(e);
      }
    }
  }

  /**
   * The class definition which defines for which mapper classes the definition shall be applied
   * 
   * @return the classDefinition
   */
  public String getClassDefinition() {
    return classDefinition;
  }

  /**
   * The class definition which defines for which mapper classes the definition shall be applied
   * 
   * @param classDefinition
   *          the classDefinition to set
   */
  public void setClassDefinition(String classDefinition) {
    this.classDefinition = classDefinition;
    init();
  }

  /**
   * If an observer shall be executed for mapper classes, which are annotated with a certain Annotation, the annotation
   * must be defined here
   * 
   * @return the annotation
   */
  public Class<? extends Annotation> getAnnotation() {
    return annotation;
  }

  /**
   * If an observer shall be executed for mapper classes, which are annotated with a certain Annotation, the annotation
   * must be defined here
   * 
   * @param annotation
   *          the annotation to set
   */
  public void setAnnotation(Class<? extends Annotation> annotation) {
    this.annotation = annotation;
  }

  /**
   * This method checks, whether the current definition is applyable to the given instance of IMapper.
   * 
   * @param mapper
   * @return true, if appliable
   */
  boolean isApplicableFor(IMapper<?> mapper) {
    boolean applyable = isApplicableFor(mapper.getMapperClass());
    if (applyable && annotation != null) {
      applyable = mapper.getAnnotation(annotation) != null;
    }
    return applyable;
  }

  /**
   * If a class definition is contained, shall the observer hit just that class or instanceof as well?
   * 
   * @return the instanceOf
   */
  public boolean isInstanceOf() {
    return instanceOf;
  }

  /**
   * If a class definition is contained, shall the observer hit just that class or instanceof as well?
   * 
   * @param instanceOf
   *          the instanceOf to set
   */
  public void setInstanceOf(boolean instanceOf) {
    this.instanceOf = instanceOf;
  }

  /**
   * This method checks, whether the current definition is applyable for the given class
   * 
   * @param mapperClass
   *          the class to be checked
   * @return if the current definition contains a class specification, it will check wether the given checkClass fits;
   *         if there is no class definition, it will return true
   */
  boolean isApplicableFor(Class<?> checkClass) {
    boolean applyable = mapperClass == null;
    if (!applyable) {
      if (isInstanceOf()) {
        applyable = this.mapperClass.isAssignableFrom(checkClass);
      } else {
        applyable = this.mapperClass == checkClass;
      }
    }
    return applyable;
  }

}
