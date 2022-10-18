package com.nphc.helper;


public class EmployeeResponseList<T> {
	
	
	private T results;
	
	
	  public EmployeeResponseList(T results) {
			super();
			this.results = results;
		}

	 

	public T getResults() {
			return results;
		}


}
