/*******************************************************************************
 * Copyright (c) 2011, Pitney Bowes Software Inc.
 * All  rights reserved.
 * Confidential Property of Pitney Bowes Software Inc.
 *
 * $Author: $
 * $Revision: $
 * $LastChangedDate: $
 *
 * $HeadURL: $
 ******************************************************************************/

package com.pb.stratus.security.core.common;

/**
 * This class contains all the types of entries/enums which are possible for Permissions.
 * We save the permissions for a configuration file in AUTH files
 */
public enum AuthorizableType
{
    MapConfig,
    CatalogConfig,
    QueryTemplates,
    BaseMap
}