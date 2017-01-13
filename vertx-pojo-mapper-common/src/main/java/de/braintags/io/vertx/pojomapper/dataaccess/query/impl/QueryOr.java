package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import de.braintags.io.vertx.pojomapper.dataaccess.query.ISearchCondition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryLogic;

/**
 * Represents a container that joins search conditions with an {@link QueryLogic#OR}<br>
 * <br>
 * Copyright: Copyright (c) 20.12.2016 <br>
 * Company: Braintags GmbH <br>
 * 
 * @author sschmitt
 */
public class QueryOr extends AbstractSearchConditionContainer {
  /**
   * Initializes the container with zero or more sub conditions that will be connected with {@link QueryLogic#OR}
   * 
   * @param searchConditions
   */
  public QueryOr(ISearchCondition... searchConditions) {
    super(searchConditions);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.ISearchConditionContainer#getQueryLogic()
   */
  @Override
  public QueryLogic getQueryLogic() {
    return QueryLogic.OR;
  }

}
