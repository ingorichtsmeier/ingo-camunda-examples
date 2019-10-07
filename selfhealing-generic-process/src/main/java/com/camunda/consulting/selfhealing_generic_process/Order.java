package com.camunda.consulting.selfhealing_generic_process;

public class Order {
  
  private String orderId;
  private String customerName;
  private String firstAddition;
  private String secondAddition; 
  
  public Order(String orderId, String customerName) {
    super();
    this.orderId = orderId;
    this.customerName = customerName;
  }
  
  public Order(String orderId, String customerName, String firstAddition, String secondAddition) {
    super();
    this.orderId = orderId;
    this.customerName = customerName;
    this.firstAddition = firstAddition;
    this.secondAddition = secondAddition;
  }

  public Order() {
    super();
  }

  public String getOrderId() {
    return orderId;
  }
  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }
  public String getCustomerName() {
    return customerName;
  }
  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getFirstAddition() {
    return firstAddition;
  }

  public void setFirstAddition(String firstAddition) {
    this.firstAddition = firstAddition;
  }

  public String getSecondAddition() {
    return secondAddition;
  }

  public void setSecondAddition(String secondAddition) {
    this.secondAddition = secondAddition;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((customerName == null) ? 0 : customerName.hashCode());
    result = prime * result + ((firstAddition == null) ? 0 : firstAddition.hashCode());
    result = prime * result + ((orderId == null) ? 0 : orderId.hashCode());
    result = prime * result + ((secondAddition == null) ? 0 : secondAddition.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Order other = (Order) obj;
    if (customerName == null) {
      if (other.customerName != null)
        return false;
    } else if (!customerName.equals(other.customerName))
      return false;
    if (firstAddition == null) {
      if (other.firstAddition != null)
        return false;
    } else if (!firstAddition.equals(other.firstAddition))
      return false;
    if (orderId == null) {
      if (other.orderId != null)
        return false;
    } else if (!orderId.equals(other.orderId))
      return false;
    if (secondAddition == null) {
      if (other.secondAddition != null)
        return false;
    } else if (!secondAddition.equals(other.secondAddition))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Order [orderId=" + orderId + ", customerName=" + customerName + ", firstAddition=" + firstAddition + ", secondAddition=" + secondAddition + "]";
  }
  
  

}
