/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import com.mysql.jdbc.jmx.LoadBalanceConnectionGroupManager;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class ConnectionGroupManager
/*     */ {
/*  36 */   private static HashMap<String, ConnectionGroup> GROUP_MAP = new HashMap();
/*     */ 
/*  38 */   private static LoadBalanceConnectionGroupManager mbean = new LoadBalanceConnectionGroupManager();
/*     */ 
/*  40 */   private static boolean hasRegisteredJmx = false;
/*     */ 
/*     */   public static synchronized ConnectionGroup getConnectionGroupInstance(String groupName)
/*     */   {
/*  44 */     if (GROUP_MAP.containsKey(groupName)) {
/*  45 */       return (ConnectionGroup)GROUP_MAP.get(groupName);
/*     */     }
/*  47 */     ConnectionGroup group = new ConnectionGroup(groupName);
/*  48 */     GROUP_MAP.put(groupName, group);
/*  49 */     return group;
/*     */   }
/*     */ 
/*     */   public static void registerJmx() throws SQLException {
/*  53 */     if (hasRegisteredJmx) {
/*  54 */       return;
/*     */     }
/*     */ 
/*  57 */     mbean.registerJmx();
/*  58 */     hasRegisteredJmx = true;
/*     */   }
/*     */ 
/*     */   public static ConnectionGroup getConnectionGroup(String groupName) {
/*  62 */     return (ConnectionGroup)GROUP_MAP.get(groupName);
/*     */   }
/*     */ 
/*     */   private static Collection<ConnectionGroup> getGroupsMatching(String group) {
/*  66 */     if ((group == null) || (group.equals(""))) {
/*  67 */       Set s = new HashSet();
/*     */ 
/*  69 */       s.addAll(GROUP_MAP.values());
/*  70 */       return s;
/*     */     }
/*  72 */     Set s = new HashSet();
/*  73 */     ConnectionGroup o = (ConnectionGroup)GROUP_MAP.get(group);
/*  74 */     if (o != null) {
/*  75 */       s.add(o);
/*     */     }
/*  77 */     return s;
/*     */   }
/*     */ 
/*     */   public static void addHost(String group, String host, boolean forExisting)
/*     */   {
/*  82 */     Collection s = getGroupsMatching(group);
/*  83 */     for (ConnectionGroup cg : s)
/*  84 */       cg.addHost(host, forExisting);
/*     */   }
/*     */ 
/*     */   public static int getActiveHostCount(String group)
/*     */   {
/*  90 */     Set active = new HashSet();
/*  91 */     Collection s = getGroupsMatching(group);
/*  92 */     for (ConnectionGroup cg : s) {
/*  93 */       active.addAll(cg.getInitialHosts());
/*     */     }
/*  95 */     return active.size();
/*     */   }
/*     */ 
/*     */   public static long getActiveLogicalConnectionCount(String group)
/*     */   {
/* 101 */     int count = 0;
/* 102 */     Collection s = getGroupsMatching(group);
/* 103 */     for (ConnectionGroup cg : s) {
/* 104 */       count = (int)(count + cg.getActiveLogicalConnectionCount());
/*     */     }
/* 106 */     return count;
/*     */   }
/*     */ 
/*     */   public static long getActivePhysicalConnectionCount(String group) {
/* 110 */     int count = 0;
/* 111 */     Collection s = getGroupsMatching(group);
/* 112 */     for (ConnectionGroup cg : s) {
/* 113 */       count = (int)(count + cg.getActivePhysicalConnectionCount());
/*     */     }
/* 115 */     return count;
/*     */   }
/*     */ 
/*     */   public static int getTotalHostCount(String group)
/*     */   {
/* 120 */     Collection s = getGroupsMatching(group);
/* 121 */     Set hosts = new HashSet();
/* 122 */     for (ConnectionGroup cg : s) {
/* 123 */       hosts.addAll(cg.getInitialHosts());
/* 124 */       hosts.addAll(cg.getClosedHosts());
/*     */     }
/* 126 */     return hosts.size();
/*     */   }
/*     */ 
/*     */   public static long getTotalLogicalConnectionCount(String group) {
/* 130 */     long count = 0L;
/* 131 */     Collection s = getGroupsMatching(group);
/* 132 */     for (ConnectionGroup cg : s) {
/* 133 */       count += cg.getTotalLogicalConnectionCount();
/*     */     }
/* 135 */     return count;
/*     */   }
/*     */ 
/*     */   public static long getTotalPhysicalConnectionCount(String group) {
/* 139 */     long count = 0L;
/* 140 */     Collection s = getGroupsMatching(group);
/* 141 */     for (ConnectionGroup cg : s) {
/* 142 */       count += cg.getTotalPhysicalConnectionCount();
/*     */     }
/* 144 */     return count;
/*     */   }
/*     */ 
/*     */   public static long getTotalTransactionCount(String group) {
/* 148 */     long count = 0L;
/* 149 */     Collection s = getGroupsMatching(group);
/* 150 */     for (ConnectionGroup cg : s) {
/* 151 */       count += cg.getTotalTransactionCount();
/*     */     }
/* 153 */     return count;
/*     */   }
/*     */ 
/*     */   public static void removeHost(String group, String host) throws SQLException {
/* 157 */     removeHost(group, host, false);
/*     */   }
/*     */ 
/*     */   public static void removeHost(String group, String host, boolean removeExisting) throws SQLException {
/* 161 */     Collection s = getGroupsMatching(group);
/* 162 */     for (ConnectionGroup cg : s)
/* 163 */       cg.removeHost(host, removeExisting);
/*     */   }
/*     */ 
/*     */   public static String getActiveHostLists(String group)
/*     */   {
/* 168 */     Collection s = getGroupsMatching(group);
/* 169 */     Map hosts = new HashMap();
/* 170 */     for (ConnectionGroup cg : s)
/*     */     {
/* 172 */       Collection l = cg.getInitialHosts();
/* 173 */       for (String host : l) {
/* 174 */         Integer o = (Integer)hosts.get(host);
/* 175 */         if (o == null)
/* 176 */           o = Integer.valueOf(1);
/*     */         else {
/* 178 */           o = Integer.valueOf(o.intValue() + 1);
/*     */         }
/* 180 */         hosts.put(host, o);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 185 */     StringBuffer sb = new StringBuffer();
/* 186 */     String sep = "";
/* 187 */     for (String host : hosts.keySet()) {
/* 188 */       sb.append(sep);
/* 189 */       sb.append(host);
/* 190 */       sb.append('(');
/* 191 */       sb.append(hosts.get(host));
/* 192 */       sb.append(')');
/* 193 */       sep = ",";
/*     */     }
/* 195 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static String getRegisteredConnectionGroups() {
/* 199 */     Collection s = getGroupsMatching(null);
/* 200 */     StringBuffer sb = new StringBuffer();
/* 201 */     String sep = "";
/* 202 */     for (ConnectionGroup cg : s) {
/* 203 */       String group = cg.getGroupName();
/* 204 */       sb.append(sep);
/* 205 */       sb.append(group);
/* 206 */       sep = ",";
/*     */     }
/* 208 */     return sb.toString();
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.ConnectionGroupManager
 * JD-Core Version:    0.6.0
 */