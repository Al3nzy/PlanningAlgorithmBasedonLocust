package org.workflowsim.examples.scheduling;
	/**
	 *
	 * Mohammed Ala'anzy 
	 *
	 * @author Ala'anzy 
	 * @since  16 November, 2022
	 */


public class JobSchedulingConstants {
		public static int current_iteration;
		public static int Algo_iteration=1;
		//public final static int num_user = 1;
		
		/*
		 * Cloudlet  types and configuration:
		 * 
		 * */
		//public final static int	 Cloudlet_number 				 = 500;
		//public final static int 	 Cloudlet_mips[]				 = { 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000 };
		//public final static int 	 Cloudlet_mips					 = 1000;
		//public final static long 	 fileSize 						 = 300;
		//public final static long 	 outputSize 					 = 300;	
		//public final static int 	 pesNumber 						 = 1;
		//public final static double CLOUDLET_LENGTH_startRandomValue= 1000; //The initial value for the random generation 
		//public final static double CLOUDLET_LENGTH_endRandomValue  = 20000;//
		//public final static int 	 CLOUDLET_PES					 = 1;
	

		/*
		 *  
		 * VM Parameters
		 */	
		public final static int		VM_number 		 = 1000;
		public final static int		VM_ram			 = 512; // vm memory (MB) 512; \\40 GB = 40000
		public final static int 	pesNumber 		 = 1; // number of cpus
		public final static String 	vmm 			 = "Xen"; // VMM name
		public final static int 	VM_BW			 = 1000;     //100000; // 100 Mbit/s
		public final static int 	VM_SIZE			 = 10000;   // image size (MB)  //2500; // 2.5 GB
		//public final static int 	VM_TYPES	   	 = 4;
		//public final static int[] VM_RAM			 = { 870,  1740, 1740, 613 };
		//public final static int[] VM_MIPS			 = createSequenceMips(VM_number);
		//public final static int[] VM_PES			 = createPE(VM_number,1); // number of CPUs
		public final static double	VM_MIPS 		 = 1000; //250 mips for paper of power 2^i mips speed.
		public final static double	BalancePercentage= 0.01; // the average percentage should be < 1.0
		public final static double	ratio			 = 0; // 0 if no increment will be applied and 1 if there is range of VM MIPS (firstRange & LastRange).
		public final static double	firstRange		 = VM_MIPS; 
		public final static double	lastRange 	 	 = 2000; 
		
		/**
		 * @return the array of VM MIPS by sending the VM number to make the array based on 
		 * 
		 */
		public static int[] createSequenceMips(int VM_number) {
			int[] VM_MIPS = new int [VM_number];
			for (int i=1; i<=VM_number;i++) {
			//	VM_MIPS[i-1]= 1000*i; // 1000 cuz I want the vm Mips in secquence of 1000-10,000
				VM_MIPS[i-1]= 2400; // 1000 cuz I want the vm Mips in secquence of 1000-10,000		
			}			
			return VM_MIPS;
		}
		
		/**
		 * @return the array of PE by sending the VM number to make the array based on
		 */
		public static int[] createPE(int VM_number, int PE) {
			int[] VM_PEs = new int [VM_number];
			for (int i=1; i<=VM_number;i++) {
				VM_PEs[i-1]= PE; // 1 cuz I want the all the PEs array =1 for each VM (each VM has 1 PE).
			}			
			return VM_PEs;		
		}
	
		/*
		 * Host Configuration:
		 *  
		 */
		public final static int		HOST_TYPES	 = 2;
		public final static int 	HOST_Number	 = 20;
		public final static int[] 	HOST_MIPS	 = {2000,2000}; // PE mips
		public final static int[] 	HOST_PES	 = {4,4};
		public final static int[] 	HOST_RAM	 = {2048,2048}; //host memory (MB) (IT WAS 2GB,,102400
		public final static int 	HOST_BW		 = 10000; //10000;
		public final static int		HOST_STORAGE = 1000000; // 1 GB// host storage

		/*
		 * Host Configuration:
		 *and its price (G$/Pe time unit).
		 */
		public final static String arch     		= "x86"; // system architecture
		public final static String os        		= "Linux"; // operating system
		public final static String DC_vmm 	 		= "Xen";
		public final static double time_zone 		= 10.0; // time zone this resource located
		public final static double cost		 		= 1.0; // the cost of using processing in this resource
		public final static double costPerMem		= 0.05; // the cost of using memory in this resource
		public final static double costPerStorage	= 0.1; // the cost of using storage in this resource
		public final static double costPerBw		= 0.1; // the cost of using bw in this resource		
	}

