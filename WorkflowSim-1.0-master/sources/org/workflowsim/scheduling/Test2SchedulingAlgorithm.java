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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.lists.VmList;
import org.workflowsim.CondorVM;
import org.workflowsim.WorkflowSimTags;

/**
 * The Locust algorithm. 
 *
 * @author Weiwei Chen
 * @since WorkflowSim Toolkit 1.0
 * @date Apr 9, 2013
 */
public class Test2SchedulingAlgorithm extends BaseSchedulingAlgorithm {
	/**
     * Initialize a Locust scheduler.
     */
    public Test2SchedulingAlgorithm() {
        super();
    }
    /**
     * The main function
     */
    @Override
    public void run() {	
    Log.printLine("  --  to get the minimam processing time required in each vm ");
    double SubmittedVmMips = 0;
   	int VmId_of_MinProcessing=0;
   	double finishing_time=0;
   	int size = getCloudletList().size();
   	int vmSize = getVmList().size();
   	DecimalFormat dft = new DecimalFormat("###.##");
    	
   	for (int i=0; i<size;i++) {  
    		Cloudlet cloudlet = (Cloudlet) getCloudletList().get(i);
			List<Integer> co =  new ArrayList<Integer>();
			CondorVM vm;
			CondorVM x=null;
			double Min_Waiting_Time = Integer.MAX_VALUE;
			Log.printLine(" ____________________________________________________________________");
			Log.printLine("   Checking the VMs to submit Cloudlet ID: #"+cloudlet.getCloudletId()+" of total length: "+cloudlet.getCloudletLength());
					for (int j=0; j<getVmList().size();j++){
							x=(CondorVM) getVmList().get(j);		
							cloudlet.setVmId(j);
							double processing_time=0;
							vm = (CondorVM) VmList.getById(getVmList(), cloudlet.getVmId());
							processing_time = (double)cloudlet.getCloudletLength() / vm.getMips();
							finishing_time= vm.getVmListFinishTimeProcessing()+processing_time;
								if (finishing_time < Min_Waiting_Time)
									{
									Min_Waiting_Time = finishing_time;
									co.add(j);
									SubmittedVmMips=vm.getMips();
									VmId_of_MinProcessing=cloudlet.getVmId();
									}
							Log.printLine(" - Vm#"+cloudlet.getVmId()+" (MIPS: "+dft.format(vm.getMips())+")  The finishing time needed is "+dft.format(finishing_time));
						}
			cloudlet.setVmId(VmId_of_MinProcessing);
			double processing_time=0;
			processing_time = (double)cloudlet.getCloudletLength() / SubmittedVmMips;
			vm = (CondorVM) VmList.getById(getVmList(), cloudlet.getVmId());
			//vm.setState(WorkflowSimTags.VM_STATUS_BUSY);
			vm.setState(WorkflowSimTags.VM_STATUS_IDLE);
			cloudlet.setVmId(VmId_of_MinProcessing);
			getScheduledList().add(cloudlet);
			finishing_time=vm.getVmListFinishTimeProcessing()+processing_time;
			vm.setVmListFinishTimeProcessing(finishing_time);
			Log.printLine("   Cloudlet #"+cloudlet.getCloudletId()+"  has been submitted to Vm# "+cloudlet.getVmId()+"   of finishing time needed is: " +dft.format(vm.getVmListFinishTimeProcessing()));	
                 break;
                }
            }
            //no vm available 
          
        }
    



