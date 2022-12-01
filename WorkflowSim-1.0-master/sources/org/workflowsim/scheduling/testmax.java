/**
 * Copyright 2012-2013 University Of Southern California
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
import org.cloudbus.cloudsim.lists.VmList;
import org.workflowsim.CondorVM;
import org.workflowsim.WorkflowSimTags;

/**
 * MaxMin algorithm.
 *
 * @author Weiwei Chen
 * @since WorkflowSim Toolkit 1.0
 * @date Apr 9, 2013
 */
public class testmax extends BaseSchedulingAlgorithm {

    /**
     * Initialize a MaxMin scheduler.
     */
    public testmax() {
        super();
    }
    /**
     * the check point list.
     */
    private List hasChecked = new ArrayList<Boolean>();

    @Override
    public void run() {

    	double Min_Waiting_Time=Integer.MAX_VALUE;
    	double finish_time=0;
        //Log.printLine("Schedulin Cycle");
        int size = getCloudletList().size();
        hasChecked.clear();
        for (int t = 0; t < size; t++) {
            boolean chk = false;
            hasChecked.add(false);
        }
        for (int i = 0; i < size; i++) {
        	double process_time=0;
            int maxIndex = 0;
            Cloudlet maxCloudlet = null;
            for (int j = 0; j < size; j++) {
                Cloudlet cloudlet = (Cloudlet) getCloudletList().get(j);
                boolean chk = (Boolean) (hasChecked.get(j));
                if (!chk) {
                    maxCloudlet = cloudlet;
                    maxIndex = j;
                    break;
                }
            }
            if (maxCloudlet == null) {
                break;
            }


            for (int j = 0; j < size; j++) {
                Cloudlet cloudlet = (Cloudlet) getCloudletList().get(j);
                boolean chk = (Boolean) (hasChecked.get(j));

                if (chk) {
                    continue;
                }

                long length = cloudlet.getCloudletLength();

                if (length > maxCloudlet.getCloudletLength()) {
                    maxCloudlet = cloudlet;
                    maxIndex = j;
                }
            }
            hasChecked.set(maxIndex, true);

            int vmSize = getVmList().size();
            CondorVM firstIdleVm = null;//(CondorVM)getVmList().get(0);
            for (int j = 0; j < vmSize; j++) {
                CondorVM vm = (CondorVM) getVmList().get(j);
                if (vm.getState() == WorkflowSimTags.VM_STATUS_IDLE) {
                    firstIdleVm = vm;
                    if (j==vmSize-1)
                    break;
                }
            }
          // finish_time=0; Min_Waiting_Time=Integer.MAX_VALUE;
            if (firstIdleVm == null) {
//            	for (int j = 0; j < vmSize; j++) {
//            		 CondorVM vm = (CondorVM) getVmList().get(j);
//                     maxCloudlet.setVmId(j);
//     				vm = (CondorVM) VmList.getById(getVmList(), maxCloudlet.getVmId());
//                     process_time=0;
//                     process_time = (double)maxCloudlet.getCloudletLength() / vm.getMips();
//     				finish_time= vm.getVmListFinishTimeProcessing()+ process_time;	
//     				if (finish_time<Min_Waiting_Time) {
//                		Min_Waiting_Time = finish_time;
//                		firstIdleVm = vm;
//                	}
//            	}
                break;
            }
            finish_time=0; Min_Waiting_Time=Integer.MAX_VALUE;
            for (int j = 0; j < vmSize; j++) {
                CondorVM vm = (CondorVM) getVmList().get(j);
                maxCloudlet.setVmId(j);
				vm = (CondorVM) VmList.getById(getVmList(), maxCloudlet.getVmId());
                process_time=0;
                process_time = (double)maxCloudlet.getCloudletLength() / vm.getMips();
				finish_time= vm.getVmListFinishTimeProcessing()+ process_time;
               if ((vm.getState() == WorkflowSimTags.VM_STATUS_IDLE)
                       && vm.getCurrentRequestedTotalMips() > firstIdleVm.getCurrentRequestedTotalMips()) {
                	if (finish_time<Min_Waiting_Time) {
                		Min_Waiting_Time = finish_time;
                		firstIdleVm = vm;
                	}
                }
            }
            firstIdleVm.setState(WorkflowSimTags.VM_STATUS_BUSY);
            maxCloudlet.setVmId(firstIdleVm.getId());
            getScheduledList().add(maxCloudlet);
            firstIdleVm.setVmListFinishTimeProcessing(finish_time);

            Log.printLine("Schedules " + maxCloudlet.getCloudletId() + " with "
                    + maxCloudlet.getCloudletLength() + " to VM " + firstIdleVm.getId()
                    + " with " + firstIdleVm.getCurrentRequestedTotalMips());

        }
    }
}
