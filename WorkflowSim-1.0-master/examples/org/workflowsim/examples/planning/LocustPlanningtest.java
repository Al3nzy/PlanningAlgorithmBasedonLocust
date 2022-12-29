/**
 * Copyright 2022-2023 University Of Putra Malaysia
 */
package org.workflowsim.examples.planning;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
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
 * This Locust Optimization Algorithm that used as a PhD work 
 *
 * @author Ala'anzy
 * @since WorkflowSim Toolkit 1.1
 * @date Oct 18, 2022
 */
public class LocustPlanningtest extends WorkflowSimBasicExample1{
	//private static double[] Averagemakespan;
	public static int iteration;
	static DecimalFormat dft = new DecimalFormat("###.##");

	////////////////////////// STATIC METHODS ///////////////////////
    protected static List<CondorVM> createVM(int userId, int vms) {

        //Creates a container to store VMs. This list is passed to the broker later
        LinkedList<CondorVM> list = new LinkedList<CondorVM>();

        //create VMs
        CondorVM[] vm = new CondorVM[vms];
        double rangePower=((JobSchedulingConstants.lastRange-JobSchedulingConstants.firstRange)/JobSchedulingConstants.firstRange)/(vms-1); //the percentage of increasements based on the first and last range of VM increasing. takeing into consideration the number of VMs. 
        double ratio=JobSchedulingConstants.ratio;
        	//Random bwRandom = new Random(System.currentTimeMillis());
      
        for (int i = 0; i < vms; i++){           
        	ratio = (rangePower* i)*JobSchedulingConstants.ratio;// I multiplied by Job....ratio to make the increment 0 in case there is fix VM MIPS where I will send the ratio 0 or 1 (0 if no increment and 1 if there is increment).
        	ratio=ratio*JobSchedulingConstants.firstRange; // the increasing for the specific VM will maintaining the First range.
        	
        	//double ratio = Math.pow(rangePower, i);// (500*1.8)(500*3.6)(500*5.4)(500*7.2)
        	//double ratio=i+1.5; //Alanzy update ratio = i+1.5; mips * ratio // (bw*ratio)
            
        	// The VM constructor that uses the DC cost model.
        	//vm[i] = new CondorVM(i, userId, JobSchedulingConstants.VM_MIPS + ratio , JobSchedulingConstants.pesNumber, JobSchedulingConstants.VM_ram, (long) (JobSchedulingConstants.VM_BW * 1), JobSchedulingConstants.VM_SIZE, JobSchedulingConstants.vmm, new CloudletSchedulerSpaceShared());
        	
        	// The VM constructor that uses the VM cost model.
        	vm[i] = new CondorVM(i, 
        			userId, 
        		//	JobSchedulingConstants.VM_MIPS + ratio ,
        			JobSchedulingConstants.VM_MIPSArr[i] ,
        			JobSchedulingConstants.pesNumber,
        			JobSchedulingConstants.VM_ram,
        			(long) (JobSchedulingConstants.VM_BW * 1),
        			JobSchedulingConstants.VM_SIZE,
        			JobSchedulingConstants.vmm,
        			JobSchedulingConstants.Vmcost,
        			JobSchedulingConstants.VariantVmcost[i], // in case there is varian of cost prices based on the Vm using
        			//JobSchedulingConstants.VmcostPerCpu,
        			JobSchedulingConstants.VmcostPerMem,
        			JobSchedulingConstants.VmcostPerStorage,
        			JobSchedulingConstants.VmcostPerBw,
        			new CloudletSchedulerSpaceShared());

            list.add(vm[i]);
        	System.out.println("Random vm" + i +" is = " + dft.format(vm[i].getMips()) +" || the ratio value is = " + dft.format(ratio));
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

        	/**
             * Should change this based on real physical path
             */
            String daxPath = JobSchedulingConstants.daxPath;//CyberShake ;Sipht_1000
            
            File daxFile = new File(daxPath);
            if(!daxFile.exists()){
                Log.printLine("Warning: Please replace daxPath with the physical path in your working environment!");
                return;
            }
            Parameters.SchedulingAlgorithm sch_method = Parameters.SchedulingAlgorithm.STATIC;
            Parameters.PlanningAlgorithm pln_method = Parameters.PlanningAlgorithm.LOCUSTPlANNING; // INVALID, RANDOM, HEFT, DHEFT, LocustTestPlanning
            ReplicaCatalog.FileSystem file_system = ReplicaCatalog.FileSystem.LOCAL;

            /**
             * Set the cost model to be VM (the default is Datacenter
             */
            Parameters.setCostModel(Parameters.CostModel.VM); // I can choose between DATACENTER & VM model

            
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
            Log.print("    The MIPS for them as follows :[");
            for (int i = 0; i < vmlist0.size(); i++) {
            	Log.print(vmlist0.get(i).getMips());
            	if (i<vmlist0.size()-1) {
            		Log.print("|");
				}    	
                vmlist0.get(i).getMips();	
                if (i >= vmlist0.size()-1)
                	Log.print("]");
			}
            
            Log.printLine();
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
