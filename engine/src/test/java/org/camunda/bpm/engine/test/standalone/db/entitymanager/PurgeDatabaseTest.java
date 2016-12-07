/*
 * Copyright 2016 camunda services GmbH.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.camunda.bpm.engine.test.standalone.db.entitymanager;

import org.camunda.bpm.engine.impl.ManagementServiceImpl;
import org.camunda.bpm.engine.impl.management.DatabasePurgeReport;
import org.camunda.bpm.engine.impl.management.PurgeReport;
import org.camunda.bpm.engine.impl.persistence.deploy.cache.CachePurgeResult;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.util.ProvidedProcessEngineRule;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Christopher Zell <christopher.zell@camunda.com>
 */
public class PurgeDatabaseTest {

  @Rule
  public ProcessEngineRule engineRule = new ProvidedProcessEngineRule();

  @Test
  public void clearTest() {
    //given data
    BpmnModelInstance test = Bpmn.createExecutableProcess("test").startEvent().endEvent().done();
    Deployment deployment = engineRule.getRepositoryService().createDeployment().addModelInstance("test.bpmn20.xml", test).deploy();
    engineRule.manageDeployment(deployment);
    engineRule.getRuntimeService().startProcessInstanceByKey("test");

    //when purge is executed
    ManagementServiceImpl managementService = (ManagementServiceImpl) engineRule.getManagementService();
    PurgeReport purge = managementService.purge();

    //then purge report should contain the removed entities
    assertFalse(purge.isEmpty());
    CachePurgeResult cachePurgeResult = purge.getCachePurgeResult();
    assertEquals(1, cachePurgeResult.getReportValue(CachePurgeResult.PROCESS_DEF_CACHE).size());

    DatabasePurgeReport databasePurgeReport = purge.getDatabasePurgeReport();
    assertEquals(1, (int) databasePurgeReport.getReportValue("ACT_RE_PROCDEF"));
    assertEquals(2, (int) databasePurgeReport.getReportValue("ACT_HI_ACTINST"));
    assertEquals(1, (int) databasePurgeReport.getReportValue("ACT_GE_BYTEARRAY"));
    assertEquals(1, (int) databasePurgeReport.getReportValue("ACT_RE_DEPLOYMENT"));
    assertEquals(1, (int) databasePurgeReport.getReportValue("ACT_HI_PROCINST"));

    //and db and cache should be cleaned
    assertEquals(0, engineRule.getRepositoryService().createProcessDefinitionQuery().count());
    assertEquals(0, engineRule.getHistoryService().createHistoricActivityInstanceQuery().count());
    assertEquals(0, engineRule.getRepositoryService().createDeploymentQuery().count());

    assertTrue(engineRule.getProcessEngineConfiguration().getDeploymentCache().getProcessDefinitionCache().isEmpty());
  }
}
