/*
 * Copyright 2018 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.cloud.services.query.rest;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.activiti.cloud.alfresco.data.domain.AlfrescoPagedResourcesAssembler;
import org.activiti.cloud.api.model.shared.CloudVariableInstance;
import org.activiti.cloud.services.query.app.repository.VariableRepository;
import org.activiti.cloud.services.query.model.ProcessVariableEntity;
import org.activiti.cloud.services.query.model.QProcessVariableEntity;
import org.activiti.cloud.services.query.rest.assembler.ProcessInstanceVariableResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        value = "/admin/v1/process-instances/{processInstanceId}/variables",
        produces = {
                MediaTypes.HAL_JSON_VALUE,
                MediaType.APPLICATION_JSON_VALUE
        })
public class ProcessInstanceVariableAdminController {

    private AlfrescoPagedResourcesAssembler<ProcessVariableEntity> pagedVariablesResourcesAssembler;

    private VariableRepository variableRepository;

    private ProcessInstanceVariableResourceAssembler variableResourceAssembler;

    @Autowired
    public ProcessInstanceVariableAdminController(VariableRepository variableRepository,
                                   ProcessInstanceVariableResourceAssembler variableResourceAssembler,
                                   AlfrescoPagedResourcesAssembler<ProcessVariableEntity> pagedVariablesResourcesAssembler) {
        this.variableRepository = variableRepository;
        this.variableResourceAssembler = variableResourceAssembler;
        this.pagedVariablesResourcesAssembler = pagedVariablesResourcesAssembler;
    }

    @RequestMapping(method = RequestMethod.GET)
    public PagedResources<Resource<CloudVariableInstance>> getVariables(@PathVariable String processInstanceId,
                                                                        @QuerydslPredicate(root = ProcessVariableEntity.class) Predicate predicate,
                                                                        Pageable pageable) {

        QProcessVariableEntity variable = QProcessVariableEntity.processVariableEntity;
        BooleanExpression expression = variable.processInstanceId.eq(processInstanceId);
        
        Predicate extendedPredicated = expression;
        if (predicate != null) {
            extendedPredicated = expression.and(predicate);
        }
        
        return pagedVariablesResourcesAssembler.toResource(pageable,
                                                           variableRepository.findAll(extendedPredicated,
                                                                                      pageable),
                                                           variableResourceAssembler);
    }
    
  
}
