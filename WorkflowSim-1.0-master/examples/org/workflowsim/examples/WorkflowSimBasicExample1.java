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
package org.workflowsim.examples;


import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.HarddriveStorage;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Makespan;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.WaitingTime;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.lists.CloudletList;
import org.cloudbus.cloudsim.lists.VmList;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.workflowsim.ClusterStorage;
import org.workflowsim.CondorVM;
import org.workflowsim.WorkflowDatacenter;
import org.workflowsim.Job;
import org.workflowsim.WorkflowEngine;
import org.workflowsim.WorkflowPlanner;
import org.workflowsim.examples.planning.HEFTPlanningAlgorithmExample1;
import org.workflowsim.examples.scheduling.JobSchedulingConstants;
import org.workflowsim.utils.ClusteringParameters;
import org.workflowsim.utils.OverheadParameters;
import org.workflowsim.utils.Parameters;
import org.workflowsim.utils.ReplicaCatalog;

/**
 * This WorkflowSimExample creates a workflow planner, a workflow engine, and
 * one schedulers, one data centers and 20 vms. You should change daxPath at least. 
 * You may change other parameters as well.
 *
 * @author Weiwei Chen
 * @since WorkflowSim Toolkit 1.0
 * @date Apr 9, 2013
 */
public class WorkflowSimBasicExample1 {
	private static double[] Averagemakespan;
	private static double[] AverageCost;
	public static int iteration;
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

        for (int i = 0; i < vms; i++) {
            double ratio = 1.0;
            vm[i] = new CondorVM(i, userId, mips * ratio, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
            list.add(vm[i]);
        }

        return list;
    }

    ////////////////////////// STATIC METHODS ///////////////////////
    /**
     * Creates main() to run this example
     * This example has only one datacenter and one storage
     */
    public static void main(String[] args) {


       try {
            // First step: Initialize the WorkflowSim package. 

            /**
             * However, the exact number of vms may not necessarily be vmNum If
             * the data center or the host doesn't have sufficient resources the
             * exact vmNum would be smaller than that. Take care.
             */
            int vmNum = 5;//number of vms;
            /**
             * Should change this based on real physical path
             */
            String daxPath = "E:/WorkflowSim-1.0-master/WorkflowSim-1.0-master/config/dax/Montage_100.xml";
            if(daxPath == null){
                Log.printLine("Warning: Please replace daxPath with the physical path in your working environment!");
                return;
            }
            File daxFile = new File(daxPath);
            if(!daxFile.exists()){
                Log.printLine("Warning: Please replace daxPath with the physical path in your working environment!");
                return;
            }

            /**
             * Since we are using MINMIN scheduling algorithm, the planning algorithm should be INVALID 
             * such that the planner would not override the result of the scheduler
             */
            Parameters.SchedulingAlgorithm sch_method = Parameters.SchedulingAlgorithm.MINMIN;
            Parameters.PlanningAlgorithm pln_method = Parameters.PlanningAlgorithm.INVALID;
            ReplicaCatalog.FileSystem file_system = ReplicaCatalog.FileSystem.SHARED;

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
            Parameters.init(vmNum, daxPath, null,
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
             * Create a list of VMs.The userId of a vm is basically the id of the scheduler
             * that controls this vm. 
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
            

        } catch (Exception e) {
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }
    }

    protected static WorkflowDatacenter createDatacenter(String name) {

        // Here are the steps needed to create a PowerDatacenter:
        // 1. We need to create a list to store one or more
        //    Machines
        List<Host> hostList = new ArrayList<Host>();

        // 2. A Machine contains one or more PEs or CPUs/Cores. Therefore, should
        //    create a list to store these PEs before creating
        //    a Machine.
        for (int i = 1; i <= 20; i++) {
            List<Pe> peList1 = new ArrayList<Pe>();
            int mips = 40000;
            // 3. Create PEs and add these into the list.
            //for a quad-core machine, a list of 4 PEs is required:
            peList1.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
            peList1.add(new Pe(1, new PeProvisionerSimple(mips)));

            int hostId = 0;
            int ram = 102400; //2048; //host memory (MB)
            long storage = 1000000; //host storage
            int bw = 100000000;
            hostList.add(
                    new Host(
                    hostId,
                    new RamProvisionerSimple(ram),
                    new BwProvisionerSimple(bw),
                    storage,
                    peList1,
                    new VmSchedulerTimeShared(peList1))); // This is our first machine
            hostId++;
        
//            hostList.add(
//                    new Host(
//                    hostId,
//                    new RamProvisionerSimple(ram),
//                    new BwProvisionerSimple(bw),
//                    storage,
//                    peList1,
//                    new VmSchedulerTimeShared(peList1))); // This is our first machin
//            hostId++;
//            
//            hostList.add(
//                    new Host(
//                    hostId,
//                    new RamProvisionerSimple(ram),
//                    new BwProvisionerSimple(bw),
//                    storage,
//                    peList1,
//                    new VmSchedulerTimeShared(peList1))); // This is our first machin
            
        }

        // 5. Create a DatacenterCharacteristics object that stores the
        //    properties of a data center: architecture, OS, list of
        //    Machines, allocation policy: time- or space-shared, time zone
        //    and its price (G$/Per time unit).

        LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now
        WorkflowDatacenter datacenter = null;


        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
        		JobSchedulingConstants.arch, 
        		JobSchedulingConstants.os, 
        		JobSchedulingConstants.DC_vmm, 
        		hostList, 
        		JobSchedulingConstants.time_zone, 
                JobSchedulingConstants.cost, 
                JobSchedulingConstants.costPerMem, 
                JobSchedulingConstants.costPerStorage, 
                JobSchedulingConstants.costPerBw);


        // 6. Finally, we need to create a storage object.
        /**
         * The bandwidth within a data center in MB/s.
         */
        int maxTransferRate = 15;// the number comes from the future grid site, you can specify your bw
        
        try {
            HarddriveStorage s1 = new HarddriveStorage(name, 1e12);
            s1.setMaxTransferRate(maxTransferRate);
            storageList.add(s1);
            datacenter = new WorkflowDatacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datacenter;
    }

    /**
     * Prints the job objects
     *
     * @param list list of jobs
     */
    protected static void printJobList(List<Job> list) {
        int size = list.size();
        Job job;
        int VmNo=JobSchedulingConstants.VM_number;
        Makespan makespan= new Makespan();
		WaitingTime waitingtime= new WaitingTime();
        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet-ID" + indent + "STATUS" + indent
                + "Datacenter-ID" + indent + "VM-ID" + indent + indent + "Time" + indent + "StartTime" + indent + "FinishTime" + indent + "Depth"+ indent + indent + "Cost");

        DecimalFormat dft = new DecimalFormat("###.##");
        double cost = 0.0;
       double c0=0; int x=0;
       double c1=0;
       double c2=0;
       double c3=0;
       for (int i = 0; i < size; i++) {
            job = list.get(i);
            Log.print(indent + job.getCloudletId() + indent + indent);
            
            if (job.getVmId()==0) {
            	 //c0=c0+ 0.15;
            	 c0=c0+(job.getActualCPUTime()*0.15);
            	 x++;
            }
            if (job.getVmId()==1) {
            	//c1=c1+ 0.3; 
            	c1=c1+(job.getActualCPUTime()*0.3);
            	x++;
            }
            if (job.getVmId()==2) {
            	//c2=c2+ 0.6; 
            	c2=c2+(job.getActualCPUTime()*0.6);
            	x++;
           }
            if (job.getVmId()==3) {
            	//c3=c3+ 0.9; 
            	c3=c3+(job.getActualCPUTime()* 0.9);
            	x++;
           }
         //   double Vmcost=0;
            
            cost += job.getProcessingCost();
            
            if (job.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");

                Log.printLine(indent + indent + job.getResourceId() + indent + indent + indent + job.getVmId()
                        + indent + indent + indent + dft.format(job.getActualCPUTime())
                        + indent + indent + dft.format(job.getExecStartTime()) + indent + indent + indent
                        + dft.format(job.getFinishTime()) + indent + indent + indent + job.getDepth()
                		+ indent + indent + indent + dft.format(job.getProcessingCost()));
                
                makespan.SavingLastFinishTimeForEachVm( VmNo,job.getVmId(), job.getFinishTime());

            } 
           
            else if (job.getCloudletStatus() == Cloudlet.FAILED) {
                Log.print("FAILED");

                Log.printLine(indent + indent + job.getResourceId() + indent + indent + indent + job.getVmId()
                        + indent + indent + indent + dft.format(job.getActualCPUTime())
                        + indent + indent + dft.format(job.getExecStartTime()) + indent + indent + indent
                        + dft.format(job.getFinishTime()) + indent + indent + indent + job.getDepth()
                        + indent + indent + indent + dft.format(job.getProcessingCost()));
            }
        }
        
		
		 // System.out.println("Number of Cloudlets (Tasks) = " + list.size());
		  //System.out.println("Cloudlet range from = "+ getMin()+" - "+ getMax() + "  MIPS   ||   for cloudletId #"+getMinId()+"  and cloudletId #"+getMaxId()+" respectivly." );
		 // System.out.println("Number of VMs = " + vmlist.size());
		 
        
        
        waitingtime.AverageWaitingTime(list);
		  System.out.println("Iteration NO. " + (JobSchedulingConstants.current_iteration +1)+ " Makespan = "+makespan.overall());
		  double averCOSTTTT=0;
		 
		averCOSTTTT=(c0+c1+c2+c3);
		  Log.printLine();
		  int JobNo=list.size()-1;
        Log.printLine("___________________________________ ");
        Log.printLine("The cost for the first 4 VMs= "+averCOSTTTT +"  for "+x+" Jobs");
        Log.printLine("The Overall Makespan is: " + dft.format(makespan.overall()));
        Log.printLine("The total cost is " + dft.format(cost));
        Log.printLine("Number of Jobs = " + JobNo);
        Log.print("Number of VMs  = " + VmNo);
       
        
       // System.out.println( "The Overall Makespan = "+makespan.overall());
   //     waitingtime.AverageWaitingTime(list);
        //setAveragemakespan(makespan.overall());
        Averagemakespan[JobSchedulingConstants.current_iteration]=makespan.overall();
        AverageCost[JobSchedulingConstants.current_iteration]=cost;
		 // Log.printLine();
		  
    }
	public static double[] getAverageCost() {
		return AverageCost;
	}

	public static void setAverageCost(double[] averageCost) {
		AverageCost = averageCost;
	}

	/**
	 * @return the averagemakespan
	 */
	public static double[] getAveragemakespan() {
		return Averagemakespan;
	}

	/**
	 * @param averagemakespan the averagemakespan to set
	 */
	public static void setAveragemakespan(double[] averagemakespan) {
		Averagemakespan = averagemakespan;
	}
}

