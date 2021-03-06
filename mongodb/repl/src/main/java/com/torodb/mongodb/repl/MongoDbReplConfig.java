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

package com.torodb.mongodb.repl;

import com.google.common.net.HostAndPort;
import com.google.inject.Injector;
import com.torodb.core.bundle.BundleConfig;
import com.torodb.core.logging.LoggerFactory;
import com.torodb.core.metrics.ToroMetricRegistry;
import com.torodb.core.supervision.Supervisor;
import com.torodb.mongodb.core.MongoDbCoreBundle;
import com.torodb.mongodb.repl.filters.ReplicationFilters;
import com.torodb.mongowp.client.wrapper.MongoClientConfiguration;

import java.util.Optional;
import java.util.concurrent.ThreadFactory;

/**
 * The configuration used by {@link MongoDbReplBundle}.
 */
public class MongoDbReplConfig implements BundleConfig {
  private final MongoDbCoreBundle coreBundle;
  private final MongoClientConfiguration mongoClientConfiguration;
  private final ReplicationFilters userReplFilter;
  private final String replSetName;
  private final ConsistencyHandler consistencyHandler;
  private final Optional<ToroMetricRegistry> metricRegistry;
  private final LoggerFactory loggerFactory;
  private final BundleConfig generalConfig;

  public MongoDbReplConfig(MongoDbCoreBundle coreBundle,
      MongoClientConfiguration mongoClientConfiguration, ReplicationFilters userReplFilter,
      String replSetName, ConsistencyHandler consistencyHandler, 
      Optional<ToroMetricRegistry> metricRegistry,
      LoggerFactory loggerFactory, BundleConfig generalConfig) {
    this.coreBundle = coreBundle;
    this.mongoClientConfiguration = mongoClientConfiguration;
    this.userReplFilter = userReplFilter;
    this.replSetName = replSetName;
    this.consistencyHandler = consistencyHandler;
    this.metricRegistry = metricRegistry;
    this.loggerFactory = loggerFactory;
    this.generalConfig = generalConfig;
  }

  public MongoDbCoreBundle getMongoDbCoreBundle() {
    return coreBundle;
  }

  public MongoClientConfiguration getMongoClientConfiguration() {
    return mongoClientConfiguration;
  }

  public ReplicationFilters getUserReplicationFilter() {
    return userReplFilter;
  }

  public String getReplSetName() {
    return replSetName;
  }

  public ConsistencyHandler getConsistencyHandler() {
    return consistencyHandler;
  }

  public Optional<ToroMetricRegistry> getMetricRegistry() {
    return metricRegistry;
  }

  public LoggerFactory getLoggerFactory() {
    return loggerFactory;
  }

  @Override
  public Injector getEssentialInjector() {
    return generalConfig.getEssentialInjector();
  }

  @Override
  public ThreadFactory getThreadFactory() {
    return generalConfig.getThreadFactory();
  }

  @Override
  public Supervisor getSupervisor() {
    return generalConfig.getSupervisor();
  }

  public HostAndPort getSyncSourceSeed() {
    return getMongoClientConfiguration().getHostAndPort();
  }
}
