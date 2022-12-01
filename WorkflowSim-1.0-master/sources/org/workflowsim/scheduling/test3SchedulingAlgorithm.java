/* Copyright 2012-2013 University Of Southern California
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.workflowsim.scheduling;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.lists.VmList;
import org.workflowsim.CondorVM;
import org.workflowsim.WorkflowSimTags;

/**
 * LOCUST Algorithm
 *
 * @author Alanzy
 * @since WorkflowSim Toolkit 1.0
 * @date Jan 28, 2022
 */
public class test3SchedulingAlgorithm extends BaseSchedulingAlgorithm {

    public test3SchedulingAlgorithm() {
        super();
    }

    @SuppressWarnings("unchecked")
	@Override
    public void run() {

    	int size = getCloudletList().size();
        double Min_Waiting_Time=Integer.MAX_VALUE;
    	double finish_time=0;
    	List<Cloudlet> mycloudletlist = new ArrayList<Cloudlet>();
    	
//    	for (int i = 0; i < size; i++) {
//    		mycloudletlist.add(i, (Cloudlet) getCloudletList().get(i));
//    		 Log.printLine("-----***----- Cloudlet ID "+mycloudletlist.get(i).getCloudletId()+"  Arraiving Time " +mycloudletlist.get(i).getSubmissionTime());
    		 //Log.printLine();
//    	}
    	
    	
    	
        for (int i = 0; i < size; i++) {
        	double process_time=0;
        	Cloudlet cloudlet = (Cloudlet) getCloudletList().get(i);
            int vmSize = getVmList().size();
            CondorVM firstIdleVm = null;
             for (int j = 0; j < vmSize; j++) {
                CondorVM vm = (CondorVM) getVmList().get(j);
                process_time = (double)cloudlet.getCloudletLength() / vm.getMips();
    			finish_time= vm.getVmListFinishTimeProcessing()+ process_time;
    			//Log.printLine("Cloudlet ID"+ cloudlet.getCloudletId()+" Finish time: " +cloudlet.getFinishTime() );
    			
                if (vm.getState() == WorkflowSimTags.VM_STATUS_IDLE)
                	//&& (finish_time<Min_Waiting_Time)) 
                {
                    firstIdleVm = vm;
//                    Min_Waiting_Time = finish_time;
                   if (j==vmSize-1)
                    break;
                }
            }
            if (firstIdleVm == null) {
                break;
            }
            
         Min_Waiting_Time=Integer.MAX_VALUE;
         finish_time=0;
            for (int j = 0; j < vmSize; j++) {
            	process_time=0;
            	CondorVM vm = (CondorVM) getVmList().get(j);
                cloudlet.setVmId(j);
				vm = (CondorVM) VmList.getById(getVmList(), cloudlet.getVmId());
				process_time = (double)cloudlet.getCloudletLength() / vm.getMips();
				finish_time= vm.getVmListFinishTimeProcessing()+ process_time;
                if 
                (
                (vm.getState() == WorkflowSimTags.VM_STATUS_IDLE)
                          && 
                           (vm.getCurrentRequestedTotalMips() > firstIdleVm.getCurrentRequestedTotalMips())
                	//vm.getTotalUtilizationOfCpuMips(CloudSim.clock())>firstIdleVm.getTotalUtilizationOfCpuMips(CloudSim.clock())
                		//vm.getCurrentAllocatedMips() > firstIdleVm.getCurrentAllocatedMips()
                		//vm.getCurrentRequestedMaxMips()>firstIdleVm.getCurrentRequestedMaxMips()
                		//) 
                	//{
                	)
                	if (finish_time<Min_Waiting_Time) {
						
                	Min_Waiting_Time = finish_time;
                    firstIdleVm = vm;
                	}
                }
            
            firstIdleVm.setState(WorkflowSimTags.VM_STATUS_BUSY);
            cloudlet.setVmId(firstIdleVm.getId());
            getScheduledList().add(cloudlet);
            firstIdleVm.setVmListFinishTimeProcessing(finish_time);
            Log.printLine("Schedules " + cloudlet.getCloudletId() + " with "
                    + cloudlet.getCloudletLength() + " to VM " + firstIdleVm.getId()
                    + " with " + firstIdleVm.getCurrentRequestedTotalMips());          
        }
    }
}
