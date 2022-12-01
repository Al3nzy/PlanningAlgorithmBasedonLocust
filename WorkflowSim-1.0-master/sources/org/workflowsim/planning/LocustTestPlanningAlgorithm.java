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
package org.workflowsim.planning;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.cloudbus.cloudsim.Log;
import org.workflowsim.CondorVM;
import org.workflowsim.Task;
import org.workflowsim.examples.scheduling.JobSchedulingConstants;

/**
 * The Random planning algorithm. This is just for demo. It is not useful in practice.
 *
 * @author Ala'anzy
 * @since WorkflowSim Toolkit 1.0
 * @date Oct 18, 2022
 */
@SuppressWarnings("unused")
public class LocustTestPlanningAlgorithm extends BasePlanningAlgorithm {
	
	/**
     * Tasks without Sorting (FCFS) function
     * @author Ala'anzy
     * @since 26 Oct 2022
	 * @param <mytask>
	 * @return Unsorted array of tasks
    */
	private Task[] unSortedTasks(List<Task> Tasks2beSorted) {
    	 Task[] mytask=new Task[Tasks2beSorted.size()];
    	for(int i=0; i<Tasks2beSorted.size();i++) {
    		 mytask[i]=Tasks2beSorted.get(i);
    	}	
    	 return mytask;
	}
	
	/**
     * Descending Sorting tasks function
     * @author Ala'anzy
     * @since 26 Oct 2022
	 * @param <mytask>
	 * @return Descending sorted array of tasks
    */
	private Task[] descendingSortingTasks(List<Task> Tasks2beSorted) {
    	 Task[] mytask=new Task[Tasks2beSorted.size()];
    	 Task[] temp=new Task[Tasks2beSorted.size()];
    	for(int i=0; i<Tasks2beSorted.size();i++) {
    		 mytask[i]=Tasks2beSorted.get(i);
    	}
    	 for (int i=0; i<mytask.length;i++) {
       	   for (int j=i+1;j<mytask.length;j++) {
       	//	   if (tl.get(r).getCloudletLength()<tl.get(j).getCloudletLength()) {
          	   if (mytask[i].getCloudletLength()<mytask[j].getCloudletLength()) {
       			temp[i]=mytask[i];
       			mytask[i]=mytask[j];
       			mytask[j]=null;
       			mytask[j]=temp[i] ;
       			temp[i]=null;
       		  }
       	   	 }
      		}	
    	 return mytask;
	}
	
	/**
     * Ascending Sorting tasks function
     * @author Ala'anzy
     * @since 26 Oct 2022
	 * @param <mytask>
	 * @return Ascending sorted array of tasks
    */
	private Task[] ascendingSortingTasks(List<Task> Tasks2beSorted) {
    	 Task[] mytask=new Task[Tasks2beSorted.size()];
    	 Task[] temp=new Task[Tasks2beSorted.size()];
    	for(int i=0; i<Tasks2beSorted.size();i++) {
    		 mytask[i]=Tasks2beSorted.get(i);
    	}
    	 for (int i=0; i<mytask.length;i++) {
       	   for (int j=i+1;j<mytask.length;j++) {
       	//	   if (tl.get(r).getCloudletLength()<tl.get(j).getCloudletLength()) {
          	   if (mytask[i].getCloudletLength()>mytask[j].getCloudletLength()) {
       			temp[i]=mytask[i];
       			mytask[i]=mytask[j];
       			mytask[j]=null;
       			mytask[j]=temp[i] ;
       			temp[i]=null;
       		  }
       	   	 }
      		}	
    	 return mytask;
	}
	
	/**
     * A function to Checking The tasks is it in the array of 
     * the smallest tasks that will be handled at end 
     * to balance the system. 
     * @author Ala'anzy
     * @since 27 Oct 2022
	 * @param The tasks and the array of smallest tasks
	 * @return True, if this value is in the 
    */
	private boolean checkTheTasks(Task[] task, int toCheckValue)
    {
        // check if the specified element is present in the array or not using contains() method		
        @SuppressWarnings("unlikely-arg-type")
		boolean test = Arrays.asList(task).contains(toCheckValue);
 
        // Print the result
        System.out.println("Is " + toCheckValue + " present in the array: " + test);
		return test;
    }
 		
	/**
     * A function to Checking The fastest VM (Finishing Time) After Assigning the cloudlet on it 
     * @author Ala'anzy
     * @since 26 Oct 2022
	 * @param Cloudlet and List of VMs
	 * @return The fastest VM
    */
	private CondorVM CheckingTheVMFinishTimeAfterAssigning(Task task) {
	     double process_time=0;
         double lowestFinishTime=Integer.MAX_VALUE;
         double finishTime=0;
         CondorVM vm =(CondorVM) getVmList().get(0);
         CondorVM vmChecking =(CondorVM) getVmList().get(0);
         for (int i = 0; i < getVmList().size(); i++) {
        	 vmChecking = (CondorVM) getVmList().get(i); 
         	// process_time= (task.getCloudletLength()/vmChecking.getMips());
         	 process_time	= 	(task.getCloudletLength()/vmChecking.getMips());
         	 finishTime		=	vmChecking.getVmListFinishTimeProcessing()+process_time;
       //   	Log.printLine("The finish Time is "+finishTime + "   task.getFinishTime= "+task.getFinishTime() +"  task.getTaskFinishTime()= "+task.getTaskFinishTime());
         	 if (finishTime<lowestFinishTime) {
         			lowestFinishTime=finishTime;
         			vm=vmChecking;
         		}
         		
			}
         vm.setVmListFinishTimeProcessing(lowestFinishTime);
    	 return vm;
	}
	
	/**
     * To update the tasks 
     * @author Ala'anzy
     * @since 27 Oct 2022
	 * @param The tasks and the array of smallest tasks
	 * @return new task array with new sequence based on the smallest task set. 
    */
	private Task[] UpdatedSequenceofTasks(Task [] sorttask, Task[] smallestTaskSet)
    {
		boolean checkNextask=false;
		Task[] newTaskSet=new Task[sorttask.length];
		int x=0;
		for(int i=0; i<sorttask.length;i++) {
			for (int j=0; j<smallestTaskSet.length;j++) {			
				if (smallestTaskSet[j].getCloudletId()==sorttask[i].getCloudletId()) {
						
					for (int a=0; a<smallestTaskSet.length;a++) {
							if(i+1!=sorttask.length && smallestTaskSet[a].getCloudletId()!=sorttask[i+1].getCloudletId() ) {
								checkNextask=true;
							}
							else { 
								checkNextask=false;
								break;
							}
					}
					if (checkNextask==true) {// means the next location is not in the smallest set of tasks
						newTaskSet[x]=sorttask[i+1];
						x++;
						i+=1;
						break;
					}								
					if (i+2!=sorttask.length) {																									
						for (int b=i+2; b<sorttask.length;b++) {
							
							for (int a=0; a<smallestTaskSet.length;a++) {
								if(smallestTaskSet[a].getCloudletId()!=sorttask[b].getCloudletId()) {
									checkNextask=true;
								}
	
								else { 
									checkNextask=false;
									break;
								}
							}
								if (checkNextask==true) {// means the next location is not in the smallest set of tasks
									newTaskSet[x]=sorttask[b];
									x++;
									i=b;
									break;
								}								
								else {
									newTaskSet[x]=null;
								}
								i=b;
								}	
						
							}
								break;											
							}	
		
					else 
					{
						if(j==smallestTaskSet.length-1) {
							newTaskSet[x]=sorttask[i];
							x++;
							}
					}															
					
				}			
		}	
		
		for (int i = 0; i < smallestTaskSet.length; i++) {
			newTaskSet[x]=smallestTaskSet[i];
			x++;
		}
      
  		return newTaskSet;
    }
	
	
	
	
	
    /**
     * The main function
     */
    @SuppressWarnings({ "unchecked" })
	@Override
    public void run() {
    	int x=-1;
        int BalancNum = (int)(getTaskList().size()*(JobSchedulingConstants.BalancePercentage));
     
        // the tasks without sorting
        Task[] taskSort=unSortedTasks(getTaskList());
    	
    	// To sort the tasks in decreasing order
//    	Task[] taskSort=descendingSortingTasks(getTaskList());
    	
    	// To sort the tasks in ascending order (upwards)
//    	Task[] taskSort=ascendingSortingTasks(getTaskList());
   
    	Task[] sortedTasks=descendingSortingTasks(getTaskList());

    	
    	Task[] smallestTasksSet = new Task[BalancNum];
    	    	
    	for (int i=0; i<BalancNum;i++) {// to get the last smallest tasks. 
    		smallestTasksSet[i]= sortedTasks[sortedTasks.length-BalancNum+i];
    	}
    	
    	Task[] LocustTasks;
		// sorting the tasks based on my Locust Algorithm    	
    	if (BalancNum==0) {
    		LocustTasks=taskSort;
    	}
    		else {
        		LocustTasks=UpdatedSequenceofTasks(taskSort,smallestTasksSet);
	
    		}
    	
//    	for (Iterator it = getTaskList().iterator(); it.hasNext();) {
//            Task task = (Task) it.next();

    	CondorVM vm = (CondorVM) getVmList().get(0);
		for (int i = 0; i < LocustTasks.length; i++) {
          x++;
  
          
   
			//double duration = task.getCloudletLength() / 1000;
 		
              
         //   for(int i = 0; i < task.getParentList().size(); i++ ){
			
         //   	Task parent = task.getParentList().get(i);
              // Log.printLine("parent no "+ i+"   cloudletId "+ parent.getCloudletId());
//            }           
          //  for(int i = 0; i < task.getChildList().size(); i++ ){
			
           // 	Task child = task.getChildList().get(i);
// 				Log.printLine("child no "+ i+"   cloudletId "+ child.getCloudletId());
            
//            } 	
   //         CondorVM vm = (CondorVM) getVmList().get(0);
            
           //selecting the Fastest VM taking into consideration the queue of submitted tasks in that VM          
            vm= CheckingTheVMFinishTimeAfterAssigning(LocustTasks[x]);
  
            LocustTasks[x].setVmId(vm.getId()); // submission the tasks onto a VM
//   		task.setVmId(vm.getId()); // submission the task to the VM                    
//	        long deadline = Parameters.getDeadline();
            
//            Log.printLine(" task ID "+ LocustTasks[x].getCloudletId()+ "||| total length= "+LocustTasks[x].getCloudletTotalLength() + "||| have submitted on VM= "+LocustTasks[x].getVmId());
//            Log.printLine(" task ID "+ LocustTasks[x].getCloudletId()+ "|| getCloudletFinishedSoFar = "+ LocustTasks[x].getCloudletFinishedSoFar()+" ||| getFinishTime=  " +LocustTasks[x].getFinishTime()+ " ||| getTaskFinishTime=  " +LocustTasks[x].getTaskFinishTime());
		}
		
		
//    	for (int i=0; i<BalancNum;i++) {// Submitting the set of smallest tasks based on balancNum onto a specific VM. 
//    		x++;
//    		LocustTasks[x].setVmId(3); 
//            Log.printLine(" task ID "+ LocustTasks[x].getCloudletId()+ "||| total length= "+LocustTasks[x].getCloudletTotalLength() + "||| have submitted on VM= "+LocustTasks[x].getVmId());   		
//    	}
    }
}