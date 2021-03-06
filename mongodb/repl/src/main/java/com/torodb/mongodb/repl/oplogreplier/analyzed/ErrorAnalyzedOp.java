/*
 * ToroDB
 * Copyright © 2014 8Kdata Technology (www.8kdata.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.torodb.mongodb.repl.oplogreplier.analyzed;

import com.torodb.kvdocument.values.KvDocument;
import com.torodb.kvdocument.values.KvValue;
import com.torodb.mongowp.commands.oplog.DeleteOplogOperation;
import com.torodb.mongowp.commands.oplog.UpdateOplogOperation;

/**
 *
 */
public class ErrorAnalyzedOp extends AbstractAnalyzedOp {

  ErrorAnalyzedOp(KvValue<?> mongoDocId) {
    super(mongoDocId, AnalyzedOpType.ERROR, null);
  }

  @Override
  public AnalyzedOp andThenInsert(KvDocument doc) {
    return this;
  }

  @Override
  public AnalyzedOp andThenUpdateMod(UpdateOplogOperation op) {
    return this;
  }

  @Override
  public AnalyzedOp andThenUpdateSet(UpdateOplogOperation op) {
    return this;
  }

  @Override
  public AnalyzedOp andThenUpsertMod(UpdateOplogOperation op) {
    return this;
  }

  @Override
  public AnalyzedOp andThenDelete(DeleteOplogOperation op) {
    return this;
  }

  @Override
  public String toString() {
    return "e(" + getMongoDocId() + ')';
  }
}
