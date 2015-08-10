package org.springframework.data.jpa.datatables.parameter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class OrderParameter {

	/**
	 * Column to which ordering should be applied. This is an index reference to
	 * the columns array of information that is also submitted to the server.
	 */
	@NotNull
	@Min(0)
	private Integer column;

	/**
	 * Ordering direction for this column. It will be asc or desc to indicate
	 * ascending ordering or descending ordering, respectively.
	 */
	@NotNull
	@Pattern(regexp = "[desc|asc]")
	private String dir;

	public OrderParameter() {
	}

	public OrderParameter(Integer column, String dir) {
		super();
		this.column = column;
		this.dir = dir;
	}

	public Integer getColumn() {
		return column;
	}

	public void setColumn(Integer column) {
		this.column = column;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

}