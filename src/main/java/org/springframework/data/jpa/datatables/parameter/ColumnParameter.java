package org.springframework.data.jpa.datatables.parameter;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

public class ColumnParameter {

	/**
	 * Column's data source
	 * 
	 * @see http://datatables.net/reference/option/columns.data
	 */
	@NotBlank
	private String data;

	/**
	 * Column's name
	 * 
	 * @see http://datatables.net/reference/option/columns.name
	 */
	private String name;

	/**
	 * Flag to indicate if this column is searchable (true) or not (false).
	 * 
	 * @see http://datatables.net/reference/option/columns.searchable
	 */
	@NotNull
	private Boolean searchable;

	/**
	 * Flag to indicate if this column is orderable (true) or not (false).
	 * 
	 * @see http://datatables.net/reference/option/columns.orderable
	 */
	@NotNull
	private Boolean orderable;

	/**
	 * Search value to apply to this specific column.
	 */
	@NotNull
	private SearchParameter search;

	public ColumnParameter() {
	}

	public ColumnParameter(String data, String name, Boolean searchable,
			Boolean orderable, SearchParameter search) {
		super();
		this.data = data;
		this.name = name;
		this.searchable = searchable;
		this.orderable = orderable;
		this.search = search;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getSearchable() {
		return searchable;
	}

	public void setSearchable(Boolean searchable) {
		this.searchable = searchable;
	}

	public Boolean getOrderable() {
		return orderable;
	}

	public void setOrderable(Boolean orderable) {
		this.orderable = orderable;
	}

	public SearchParameter getSearch() {
		return search;
	}

	public void setSearch(SearchParameter search) {
		this.search = search;
	}

}