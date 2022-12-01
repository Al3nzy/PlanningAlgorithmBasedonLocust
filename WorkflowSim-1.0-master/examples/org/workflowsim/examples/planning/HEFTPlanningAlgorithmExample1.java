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
package org.workflowsim.examples.planning;

import java.io.File;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.workflowsim.CondorVM;
import org.workflowsim.WorkflowDatacenter;
import org.workflowsim.Job;
import org.workflowsim.WorkflowEngine;
import org.workflowsim.WorkflowPlanner;
import org.workflowsim.examples.WorkflowSimBasicExample1;
import org.workflowsim.examples.scheduling.JobSchedulingConstants;
import org.workflowsim.utils.ClusteringParameters;
import org.workflowsim.utils.OverheadParameters;
import org.workflowsim.utils.Parameters;
import org.workflowsim.utils.ReplicaCatalog;

/**
 * This DHEFTPlanningAlgorithmExample1 creates a workflow planner, a workflow
 * engine, and one schedulers, one data centers and 20 heterogeneous vms that
 * has different communication cost (such that HEFT algorithm should work)
 *
 * @author Weiwei Chen
 * @since WorkflowSim Toolkit 1.1
 * @date Nov 9, 2013
 */
public class HEFTPlanningAlgorithmExample1 extends WorkflowSimBasicExample1{
	//private static double[] Averagemakespan;
	public static int iteration;


	////////////////////////// STATIC METHODS ///////////////////////
    protected static List<CondorVM> createVM(int userId, int vms) {

        //Creates a container to store VMs. This list is passed to the broker later
        LinkedList<CondorVM> list = new LinkedList<CondorVM>();

        //VM Parameters
        long size = 10000; //image size (MB)
        int ram = 512; //vm memory (MB)
        int mips = 1000;
        long bw = 1000;
        int pesNumber = 1; //number of cpus
        String vmm = "Xen"; //VMM name

        //create VMs
        CondorVM[] vm = new CondorVM[vms];

        Random bwRandom = new Random(System.currentTimeMillis());
      
        for (int i = 0; i < vms; i++) {            
        	//double ratio = bwRandom.nextDouble(); //to get range 0<ratio<1
        	double ratio=1.0;
//        	double ratio = Math.pow(2, i);// 2-4-8 (250*2)(250*4)(250*8)
        	System.out.println("Random vm" + i +" is = " + JobSchedulingConstants.VM_MIPS * ratio +" || the ratio value is = " + ratio);
         //   double ratio=i+1.5; //alanzy update ratio=i+1.5; mips * ratio // (bw * ratio)
            vm[i] = new CondorVM(i, userId, JobSchedulingConstants.VM_MIPS * ratio , JobSchedulingConstants.pesNumber, JobSchedulingConstants.VM_ram, (long) (JobSchedulingConstants.VM_BW * 1), JobSchedulingConstants.VM_SIZE, JobSchedulingConstants.vmm, new CloudletSchedulerSpaceShared());
            list.add(vm[i]);
        }

        return list;
    }

    
    
    
    
    /**
     * Creates main() to run this example This example has only one datacenter
     * and one storage
     */
    public static void main(String[] args) {
    	//getAveragemakespan();
    	JobSchedulingConstants.current_iteration=0;
    	for (JobSchedulingConstants.current_iteration=0; JobSchedulingConstants.current_iteration<JobSchedulingConstants.Algo_iteration;JobSchedulingConstants.current_iteration++ ) {
			Log.printLine("Starting Locust... \n          Iteration No. "+ (JobSchedulingConstants.current_iteration+1) );
			if (JobSchedulingConstants.current_iteration==0) {
				
				double[] makespan=new double[JobSchedulingConstants.Algo_iteration];
				double[] Cost=new double[JobSchedulingConstants.Algo_iteration];

				setAveragemakespan(makespan);
				setAverageCost(Cost);
			}
        
        try {
            // First step: Initialize the WorkflowSim package. 

            /**
             * However, the exact number of vms may not necessarily be vmNum If
             * the data center or the host doesn't have sufficient resources the
             * exact vmNum would be smaller than that. Take care.
             */
          //  int vmNum = 5;//number of vms;
            /**
             * Should change this based on real physical path
             */
            String daxPath = "E:/WorkflowSim-1.0-master/WorkflowSim-1.0-master/config/dax/Montage_1000.xml";//CyberShake ;Sipht_1000
            
            File daxFile = new File(daxPath);
            if(!daxFile.exists()){
                Log.printLine("Warning: Please replace daxPath with the physical path in your working environment!");
                return;
            }

            /**
             * Since we are using HEFT planning algorithm, the scheduling algorithm should be static 
             * such that the scheduler would not override the result of the planner
             */
            Parameters.SchedulingAlgorithm sch_method = Parameters.SchedulingAlgorithm.STATIC;
            Parameters.PlanningAlgorithm pln_method = Parameters.PlanningAlgorithm.HEFT;
            ReplicaCatalog.FileSystem file_system = ReplicaCatalog.FileSystem.LOCAL;

            /**
             * No overheads 
             */
            OverheadParameters op = new OverheadParameters(0, null, null, null, null, 0);;
            
            /**
             * No Clustering
             */
            ClusteringParameters.ClusteringMethod method = ClusteringParameters.ClusteringMethod.NONE;
            ClusteringParameters cp = new ClusteringParameters(0, 0, method, null);

            /**
             * Initialize static parameters
             */
            Parameters.init(JobSchedulingConstants.VM_number, daxPath, null,
                    null, op, cp, sch_method, pln_method,
                    null, 0);
            ReplicaCatalog.init(file_system);

            // before creating any entities.
            int num_user = 1;   // number of grid users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;  // mean trace events

            // Initialize the CloudSim library
            CloudSim.init(num_user, calendar, trace_flag);

            WorkflowDatacenter datacenter0 = createDatacenter("Datacenter_0");

            /**
             * Create a WorkflowPlanner with one schedulers.
             */
            WorkflowPlanner wfPlanner = new WorkflowPlanner("planner_0", 1);
            /**
             * Create a WorkflowEngine.
             */
            WorkflowEngine wfEngine = wfPlanner.getWorkflowEngine();
            /**
             * Create a list of VMs.The userId of a vm is basically the id of
             * the scheduler that controls this vm.
             */
            List<CondorVM> vmlist0 = createVM(wfEngine.getSchedulerId(0), Parameters.getVmNum());

            /**
             * Submits this list of vms to this WorkflowEngine.
             */
            wfEngine.submitVmList(vmlist0, 0);

            /**
             * Binds the data centers with the scheduler.
             */
            wfEngine.bindSchedulerDatacenter(datacenter0.getId(), 0);

            CloudSim.startSimulation();


            List<Job> outputList0 = wfEngine.getJobsReceivedList();

            CloudSim.stopSimulation();

            printJobList(outputList0);
            Log.printLine(" Simulation has been done!");
			Log.printLine();
			Log.printLine("_______________________________________________");

        } catch (Exception e) {
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }
    }

		double overall=0;
		double Makespan=0;
		double cost=0;
		double totalCost=0;
		double [] CostIndex=getAverageCost();
		double[] makespanindix=getAveragemakespan();
		for(int i=0; i<getAveragemakespan().length; i++){
			overall = overall + makespanindix[i];
			totalCost=totalCost+CostIndex[i];
	    }   

		Makespan=overall / makespanindix.length;
		cost=totalCost/CostIndex.length;
		Log.printLine( "Average Makespan for "+JobSchedulingConstants.current_iteration+" iterations is = "+  Makespan);
		Log.printLine( "Average Cost for "+JobSchedulingConstants.current_iteration+" iterations is = "+  cost);

}



}
