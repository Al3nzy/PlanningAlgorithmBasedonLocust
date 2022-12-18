package org.cloudbus.cloudsim;

public class Makespan {
	private int vmListSize;
	private double[] saving;

	public Makespan() {
		System.out.println("Calculting the Makespan...");
	}


	public  void SavingLastFinishTimeForEachVm( int vmListSize,int VmID, double finishtime) {
		setVmListSize(vmListSize);
		double[] saving2 = new double [vmListSize];
		if (getSaving()!=null)
		{
			 saving2 =getSaving();
		}
			
		saving2[VmID]=(double)finishtime; // here we will save the vm id with the last finish time for it 
		setSaving (saving2);  
		
	}

public  double overall() {
	double overall=0;
	int NumberOfUsedVm=0;
	for(int i=0; i<saving.length; i++){
		overall = (double)overall + saving[i];
    } 
	 for (int i = 0; i < saving.length; i++) {
		if (saving[i] != 0) {
			NumberOfUsedVm++;
		}
	}
	return (double) overall / NumberOfUsedVm;
   
}
public int getVmListSize() {
	return vmListSize;
}


public void setVmListSize(int vmListSize) {
	this.vmListSize = vmListSize;
}
public double[] getSaving() {
	return saving;
}


public void setSaving(double[] saving) {
	this.saving = saving;
}
}
