/*
 * Copyright (c) 2002-2020, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.suggest.business;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.portal.business.style.Theme;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.portal.ThemesService;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * class SuggestDAO
 */
public final class SuggestDAO implements ISuggestDAO
{
    // Constants
    private static final String SQL_COLS_UNAVAILABILITY_WORKGROUP_WORKFLOW = "unavailability_message,workgroup,id_workflow,";
    private static final String SQL_COLS_VOTE_TABLE = "id_vote_type,number_vote_required,number_day_required,active_suggest_submit_authentification, ";
    private static final String SQL_COLS_AUTHENTICATION = "active_vote_authentification,active_comment_authentification,disable_new_suggest_submit, ";
    private static final String SQL_COLS_COMMENTS = "authorized_comment, disable_new_comment ,id_mailing_list_suggest_submit, ";
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_suggest ) FROM suggest_suggest";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_suggest,title,"
            + SQL_COLS_UNAVAILABILITY_WORKGROUP_WORKFLOW
            + SQL_COLS_VOTE_TABLE
            + SQL_COLS_AUTHENTICATION
            + SQL_COLS_COMMENTS
            + "active_captcha,active, date_creation, libelle_validate_button,active_suggest_proposition_state,libelle_contribution, "
            + "number_suggest_submit_in_top_score,number_suggest_submit_in_top_comment,limit_number_vote,number_suggest_submit_caracters_shown, "
            + "show_category_block,show_top_score_block,show_top_comment_block,active_suggest_submit_paginator,number_suggest_submit_per_page,role, "
            + "enable_new_suggest_submit_mail,header,sort_field,code_theme,confirmation_message,active_editor_bbcode, "
            + "default_suggest,id_default_sort,notification_new_comment_sender,notification_new_comment_title,notification_new_comment_body,notification_new_suggest_submit_sender,notification_new_suggest_submit_title,notification_new_suggest_submit_body "
            + "FROM suggest_suggest WHERE id_suggest = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO suggest_suggest ( id_suggest,title,"
            + SQL_COLS_UNAVAILABILITY_WORKGROUP_WORKFLOW
            + SQL_COLS_VOTE_TABLE
            + SQL_COLS_AUTHENTICATION
            + SQL_COLS_COMMENTS
            + "active_captcha,active, date_creation, libelle_validate_button,active_suggest_proposition_state, "
            + "libelle_contribution,number_suggest_submit_in_top_score,number_suggest_submit_in_top_comment,limit_number_vote, "
            + "number_suggest_submit_caracters_shown,show_category_block,show_top_score_block,show_top_comment_block ,active_suggest_submit_paginator,number_suggest_submit_per_page,role,"
            + "enable_new_suggest_submit_mail,header,sort_field,code_theme,confirmation_message,active_editor_bbcode,default_suggest,id_default_sort,notification_new_comment_sender,notification_new_comment_title,notification_new_comment_body,notification_new_suggest_submit_sender,notification_new_suggest_submit_title,notification_new_suggest_submit_body)"
            + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM suggest_suggest WHERE id_suggest = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE suggest_suggest SET  id_suggest=?,title=?," + "unavailability_message=?,workgroup=?,id_workflow=?,"
            + "id_vote_type=?,number_vote_required=?,number_day_required=?,active_suggest_submit_authentification=?, "
            + "active_vote_authentification=?,active_comment_authentification=?,disable_new_suggest_submit=?, "
            + "authorized_comment=?, disable_new_comment=? ,id_mailing_list_suggest_submit=?, "
            + "active_captcha=?,active=?, date_creation=?, libelle_validate_button=? ,active_suggest_proposition_state=?,"
            + "libelle_contribution=? ,number_suggest_submit_in_top_score=?,number_suggest_submit_in_top_comment=?,"
            + "limit_number_vote=?,number_suggest_submit_caracters_shown=?,  " + "show_category_block=?,show_top_score_block=?,show_top_comment_block=?  ,"
            + "active_suggest_submit_paginator=?,number_suggest_submit_per_page=? ,role=? ,"
            + "enable_new_suggest_submit_mail=?,header=? ,sort_field=? ,code_theme=?, confirmation_message=?,active_editor_bbcode=? ,"
            + "default_suggest=?,id_default_sort=?,notification_new_comment_sender=?,notification_new_comment_title=?,"
            + "notification_new_comment_body=?,notification_new_suggest_submit_sender=?,notification_new_suggest_submit_title=?"
            + ",notification_new_suggest_submit_body=? " + "WHERE id_suggest=?";
    private static final String SQL_QUERY_SELECT_SUGGEST_BY_FILTER = "SELECT id_suggest,title,"
            + SQL_COLS_UNAVAILABILITY_WORKGROUP_WORKFLOW
            + SQL_COLS_VOTE_TABLE
            + SQL_COLS_AUTHENTICATION
            + SQL_COLS_COMMENTS
            + "active_captcha,active, date_creation, libelle_validate_button,active_suggest_proposition_state,libelle_contribution, "
            + "number_suggest_submit_in_top_score,number_suggest_submit_in_top_comment,limit_number_vote,number_suggest_submit_caracters_shown, "
            + "show_category_block,show_top_score_block,show_top_comment_block, active_suggest_submit_paginator,number_suggest_submit_per_page,role,  "
            + "enable_new_suggest_submit_mail,header, sort_field, code_theme, confirmation_message,active_editor_bbcode, "
            + "default_suggest,id_default_sort,notification_new_comment_sender,notification_new_comment_title,notification_new_comment_body,notification_new_suggest_submit_sender,notification_new_suggest_submit_title,notification_new_suggest_submit_body "
            + " FROM suggest_suggest ";
    private static final String SQL_QUERY_SELECT_ALL_THEMES = "SELECT id_suggest, code_theme FROM suggest_suggest";
    private static final String SQL_FILTER_WORKGROUP = " workgroup = ? ";
    private static final String SQL_FILTER_ROLE = " role = ? ";
    private static final String SQL_FILTER_STATE = " active = ? ";
    private static final String SQL_FILTER_DEFAULT_SUGGEST = " default_suggest = ? ";
    private static final String SQL_ORDER_BY_DATE_CREATION = " ORDER BY date_creation  DESC ";
    private static final String SQL_QUERY_UPDATE_SUGGEST_ORDER = "UPDATE suggest_suggest SET sort_field = ? WHERE id_suggest = ?";

    /**
     * Generates a new primary key
     *
     * @param plugin
     *            the plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin ) )
        {
            daoUtil.executeQuery( );

            daoUtil.next( );
            return daoUtil.getInt( 1 ) + 1;
        }
    }

    /**
     * Insert a new record in the table.
     *
     * @param suggest
     *            instance of the Suggest to insert
     * @param plugin
     *            the plugin
     * @return the new suggest create
     */
    @Override
    public int insert( Suggest suggest, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            Timestamp timestamp = new java.sql.Timestamp( new java.util.Date( ).getTime( ) );

            suggest.setIdSuggest( newPrimaryKey( plugin ) );

            int ncpt = 1;
            daoUtil.setInt( ncpt++, suggest.getIdSuggest( ) );
            daoUtil.setString( ncpt++, suggest.getTitle( ) );
            daoUtil.setString( ncpt++, suggest.getUnavailabilityMessage( ) );
            daoUtil.setString( ncpt++, suggest.getWorkgroup( ) );
            daoUtil.setInt( ncpt++, suggest.getIdWorkflow( ) );
            daoUtil.setInt( ncpt++, suggest.getVoteType( ).getIdVoteType( ) );
            daoUtil.setInt( ncpt++, suggest.getNumberVoteRequired( ) );
            daoUtil.setInt( ncpt++, suggest.getNumberDayRequired( ) );
            daoUtil.setBoolean( ncpt++, suggest.isActiveSuggestSubmitAuthentification( ) );
            daoUtil.setBoolean( ncpt++, suggest.isActiveVoteAuthentification( ) );
            daoUtil.setBoolean( ncpt++, suggest.isActiveCommentAuthentification( ) );
            daoUtil.setBoolean( ncpt++, suggest.isDisableNewSuggestSubmit( ) );
            daoUtil.setBoolean( ncpt++, suggest.isAuthorizedComment( ) );
            daoUtil.setBoolean( ncpt++, suggest.isDisableNewComment( ) );
            daoUtil.setInt( ncpt++, suggest.getIdMailingListSuggestSubmit( ) );
            daoUtil.setBoolean( ncpt++, suggest.isActiveCaptcha( ) );
            daoUtil.setBoolean( ncpt++, suggest.isActive( ) );
            daoUtil.setTimestamp( ncpt++, timestamp );
            daoUtil.setString( ncpt++, suggest.getLibelleValidateButton( ) );
            daoUtil.setBoolean( ncpt++, suggest.isActiveSuggestPropositionState( ) );
            daoUtil.setString( ncpt++, suggest.getLibelleContribution( ) );
            daoUtil.setInt( ncpt++, suggest.getNumberSuggestSubmitInTopScore( ) );
            daoUtil.setInt( ncpt++, suggest.getNumberSuggestSubmitInTopComment( ) );
            daoUtil.setBoolean( ncpt++, suggest.isLimitNumberVote( ) );
            daoUtil.setInt( ncpt++, suggest.getNumberSuggestSubmitCaractersShown( ) );
            daoUtil.setBoolean( ncpt++, suggest.isShowCategoryBlock( ) );
            daoUtil.setBoolean( ncpt++, suggest.isShowTopScoreBlock( ) );
            daoUtil.setBoolean( ncpt++, suggest.isShowTopCommentBlock( ) );
            daoUtil.setBoolean( ncpt++, suggest.isActiveSuggestSubmitPaginator( ) );
            daoUtil.setInt( ncpt++, suggest.getNumberSuggestSubmitPerPage( ) );
            daoUtil.setString( ncpt++, suggest.getRole( ) );
            daoUtil.setBoolean( ncpt++, suggest.isEnableMailNewSuggestSubmit( ) );
            daoUtil.setString( ncpt++, suggest.getHeader( ) );
            daoUtil.setInt( ncpt++, suggest.getSortField( ) );
            daoUtil.setString( ncpt++, suggest.getCodeTheme( ) );
            daoUtil.setString( ncpt++, suggest.getConfirmationMessage( ) );
            daoUtil.setBoolean( ncpt++, suggest.isActiveEditorBbcode( ) );
            daoUtil.setBoolean( ncpt++, suggest.isDefaultSuggest( ) );
            daoUtil.setInt( ncpt++, suggest.getIdDefaultSort( ) );
            daoUtil.setString( ncpt++, suggest.getNotificationNewCommentSenderName( ) );
            daoUtil.setString( ncpt++, suggest.getNotificationNewCommentTitle( ) );
            daoUtil.setString( ncpt++, suggest.getNotificationNewCommentBody( ) );
            daoUtil.setString( ncpt++, suggest.getNotificationNewSuggestSubmitSenderName( ) );
            daoUtil.setString( ncpt++, suggest.getNotificationNewSuggestSubmitTitle( ) );
            daoUtil.setString( ncpt++, suggest.getNotificationNewSuggestSubmitBody( ) );
            daoUtil.executeUpdate( );

            return suggest.getIdSuggest( );
        }
    }

    /**
     * Load the data of the suggest from the table
     *
     * @param nId
     *            The identifier of the suggest
     * @param plugin
     *            the plugin
     * @return the instance of the Suggest
     */
    @Override
    public Suggest load( int nId, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin ) )
        {
            daoUtil.setInt( 1, nId );
            daoUtil.executeQuery( );

            VoteType voteType;
            Suggest suggest = null;

            if ( daoUtil.next( ) )
            {
                int nIndex = 1;
                suggest = new Suggest( );
                suggest.setIdSuggest( daoUtil.getInt( nIndex++ ) );
                suggest.setTitle( daoUtil.getString( nIndex++ ) );
                suggest.setUnavailabilityMessage( daoUtil.getString( nIndex++ ) );
                suggest.setWorkgroup( daoUtil.getString( nIndex++ ) );
                suggest.setIdWorkflow( daoUtil.getInt( nIndex++ ) );

                voteType = new VoteType( );
                voteType.setIdVoteType( daoUtil.getInt( nIndex++ ) );
                suggest.setVoteType( voteType );

                suggest.setNumberVoteRequired( daoUtil.getInt( nIndex++ ) );
                suggest.setNumberDayRequired( daoUtil.getInt( nIndex++ ) );
                suggest.setActiveSuggestSubmitAuthentification( daoUtil.getBoolean( nIndex++ ) );
                suggest.setActiveVoteAuthentification( daoUtil.getBoolean( nIndex++ ) );
                suggest.setActiveCommentAuthentification( daoUtil.getBoolean( nIndex++ ) );
                suggest.setDisableNewSuggestSubmit( daoUtil.getBoolean( nIndex++ ) );
                suggest.setAuthorizedComment( daoUtil.getBoolean( nIndex++ ) );
                suggest.setDisableNewComment( daoUtil.getBoolean( nIndex++ ) );
                suggest.setIdMailingListSuggestSubmit( daoUtil.getInt( nIndex++ ) );
                suggest.setActiveCaptcha( daoUtil.getBoolean( nIndex++ ) );
                suggest.setActive( daoUtil.getBoolean( nIndex++ ) );
                suggest.setDateCreation( daoUtil.getTimestamp( nIndex++ ) );
                suggest.setLibelleValidateButton( daoUtil.getString( nIndex++ ) );
                suggest.setActiveSuggestPropositionState( daoUtil.getBoolean( nIndex++ ) );
                suggest.setLibelleContribution( daoUtil.getString( nIndex++ ) );
                suggest.setNumberSuggestSubmitInTopScore( daoUtil.getInt( nIndex++ ) );
                suggest.setNumberSuggestSubmitInTopComment( daoUtil.getInt( nIndex++ ) );
                suggest.setLimitNumberVote( daoUtil.getBoolean( nIndex++ ) );
                suggest.setNumberSuggestSubmitCaractersShown( daoUtil.getInt( nIndex++ ) );
                suggest.setShowCategoryBlock( daoUtil.getBoolean( nIndex++ ) );
                suggest.setShowTopScoreBlock( daoUtil.getBoolean( nIndex++ ) );
                suggest.setShowTopCommentBlock( daoUtil.getBoolean( nIndex++ ) );
                suggest.setActiveSuggestSubmitPaginator( daoUtil.getBoolean( nIndex++ ) );
                suggest.setNumberSuggestSubmitPerPage( daoUtil.getInt( nIndex++ ) );
                suggest.setRole( daoUtil.getString( nIndex++ ) );
                suggest.setEnableMailNewSuggestSubmit( daoUtil.getBoolean( nIndex++ ) );
                suggest.setHeader( daoUtil.getString( nIndex++ ) );
                suggest.setSortField( daoUtil.getInt( nIndex++ ) );
                suggest.setCodeTheme( daoUtil.getString( nIndex++ ) );
                suggest.setConfirmationMessage( daoUtil.getString( nIndex++ ) );
                suggest.setActiveEditorBbcode( daoUtil.getBoolean( nIndex++ ) );
                suggest.setDefaultSuggest( daoUtil.getBoolean( nIndex++ ) );
                suggest.setIdDefaultSort( daoUtil.getInt( nIndex++ ) );
                suggest.setNotificationNewCommentSenderName( daoUtil.getString( nIndex++ ) );
                suggest.setNotificationNewCommentTitle( daoUtil.getString( nIndex++ ) );
                suggest.setNotificationNewCommentBody( daoUtil.getString( nIndex++ ) );
                suggest.setNotificationNewSuggestSubmitSenderName( daoUtil.getString( nIndex++ ) );
                suggest.setNotificationNewSuggestSubmitTitle( daoUtil.getString( nIndex++ ) );
                suggest.setNotificationNewSuggestSubmitBody( daoUtil.getString( nIndex++ ) );
            }

            return suggest;
        }
    }

    /**
     * Delete a record from the table
     *
     * @param nIdSuggest
     *            The identifier of the suggest
     * @param plugin
     *            the plugin
     */
    @Override
    public void delete( int nIdSuggest, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdSuggest );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Update the suggest in the table
     *
     * @param suggest
     *            instance of the suggest object to update
     * @param plugin
     *            the plugin
     */
    @Override
    public void store( Suggest suggest, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;
            suggest.setIdSuggest( suggest.getIdSuggest( ) );
            daoUtil.setInt( nIndex++, suggest.getIdSuggest( ) );
            daoUtil.setString( nIndex++, suggest.getTitle( ) );
            daoUtil.setString( nIndex++, suggest.getUnavailabilityMessage( ) );
            daoUtil.setString( nIndex++, suggest.getWorkgroup( ) );
            daoUtil.setInt( nIndex++, suggest.getIdWorkflow( ) );
            daoUtil.setInt( nIndex++, suggest.getVoteType( ).getIdVoteType( ) );
            daoUtil.setInt( nIndex++, suggest.getNumberVoteRequired( ) );
            daoUtil.setInt( nIndex++, suggest.getNumberDayRequired( ) );
            daoUtil.setBoolean( nIndex++, suggest.isActiveSuggestSubmitAuthentification( ) );
            daoUtil.setBoolean( nIndex++, suggest.isActiveVoteAuthentification( ) );
            daoUtil.setBoolean( nIndex++, suggest.isActiveCommentAuthentification( ) );
            daoUtil.setBoolean( nIndex++, suggest.isDisableNewSuggestSubmit( ) );
            daoUtil.setBoolean( nIndex++, suggest.isAuthorizedComment( ) );
            daoUtil.setBoolean( nIndex++, suggest.isDisableNewComment( ) );
            daoUtil.setInt( nIndex++, suggest.getIdMailingListSuggestSubmit( ) );
            daoUtil.setBoolean( nIndex++, suggest.isActiveCaptcha( ) );
            daoUtil.setBoolean( nIndex++, suggest.isActive( ) );
            daoUtil.setTimestamp( nIndex++, suggest.getDateCreation( ) );
            daoUtil.setString( nIndex++, suggest.getLibelleValidateButton( ) );
            daoUtil.setBoolean( nIndex++, suggest.isActiveSuggestPropositionState( ) );
            daoUtil.setString( nIndex++, suggest.getLibelleContribution( ) );
            daoUtil.setInt( nIndex++, suggest.getNumberSuggestSubmitInTopScore( ) );
            daoUtil.setInt( nIndex++, suggest.getNumberSuggestSubmitInTopComment( ) );
            daoUtil.setBoolean( nIndex++, suggest.isLimitNumberVote( ) );
            daoUtil.setInt( nIndex++, suggest.getNumberSuggestSubmitCaractersShown( ) );
            daoUtil.setBoolean( nIndex++, suggest.isShowCategoryBlock( ) );
            daoUtil.setBoolean( nIndex++, suggest.isShowTopScoreBlock( ) );
            daoUtil.setBoolean( nIndex++, suggest.isShowTopCommentBlock( ) );
            daoUtil.setBoolean( nIndex++, suggest.isActiveSuggestSubmitPaginator( ) );
            daoUtil.setInt( nIndex++, suggest.getNumberSuggestSubmitPerPage( ) );
            daoUtil.setString( nIndex++, suggest.getRole( ) );
            daoUtil.setBoolean( nIndex++, suggest.isEnableMailNewSuggestSubmit( ) );
            daoUtil.setString( nIndex++, suggest.getHeader( ) );
            daoUtil.setInt( nIndex++, suggest.getSortField( ) );
            daoUtil.setString( nIndex++, suggest.getCodeTheme( ) );
            daoUtil.setString( nIndex++, suggest.getConfirmationMessage( ) );
            daoUtil.setBoolean( nIndex++, suggest.isActiveEditorBbcode( ) );
            daoUtil.setBoolean( nIndex++, suggest.isDefaultSuggest( ) );
            daoUtil.setInt( nIndex++, suggest.getIdDefaultSort( ) );
            daoUtil.setString( nIndex++, suggest.getNotificationNewCommentSenderName( ) );
            daoUtil.setString( nIndex++, suggest.getNotificationNewCommentTitle( ) );
            daoUtil.setString( nIndex++, suggest.getNotificationNewCommentBody( ) );
            daoUtil.setString( nIndex++, suggest.getNotificationNewSuggestSubmitSenderName( ) );
            daoUtil.setString( nIndex++, suggest.getNotificationNewSuggestSubmitTitle( ) );
            daoUtil.setString( nIndex++, suggest.getNotificationNewSuggestSubmitBody( ) );

            daoUtil.setInt( nIndex++, suggest.getIdSuggest( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Load the data of all the suggest who verify the filter and returns them in a list
     * 
     * @param filter
     *            the filter
     * @param plugin
     *            the plugin
     * @return the list of suggest
     */
    @Override
    public List<Suggest> selectSuggestList( SuggestFilter filter, Plugin plugin )
    {
        List<Suggest> suggestList = new ArrayList<>( );
        Suggest suggest;
        VoteType voteType;
        List<String> listStrFilter = new ArrayList<>( );
        int ncpt;

        if ( filter.containsWorkgroupCriteria( ) )
        {
            listStrFilter.add( SQL_FILTER_WORKGROUP );
        }

        if ( filter.containsRoleCriteria( ) )
        {
            listStrFilter.add( SQL_FILTER_ROLE );
        }

        if ( filter.containsIdState( ) )
        {
            listStrFilter.add( SQL_FILTER_STATE );
        }

        if ( filter.containsIdDefaultSuggest( ) )
        {
            listStrFilter.add( SQL_FILTER_DEFAULT_SUGGEST );
        }

        String strSQL = SuggestUtils.buildRequestWithFilter( SQL_QUERY_SELECT_SUGGEST_BY_FILTER, listStrFilter, SQL_ORDER_BY_DATE_CREATION );
        try( DAOUtil daoUtil = new DAOUtil( strSQL, plugin ) )
        {
            int nIndex = 1;

            if ( filter.containsWorkgroupCriteria( ) )
            {
                daoUtil.setString( nIndex, filter.getWorkgroup( ) );
                nIndex++;
            }

            if ( filter.containsRoleCriteria( ) )
            {
                daoUtil.setString( nIndex, filter.getRole( ) );
                nIndex++;
            }

            if ( filter.containsIdState( ) )
            {
                daoUtil.setInt( nIndex, filter.getIdState( ) );
                nIndex++;
            }

            if ( filter.containsIdDefaultSuggest( ) )
            {
                daoUtil.setInt( nIndex, filter.getIdDefaultSuggest( ) );
                nIndex++;
            }

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                ncpt = 1;
                suggest = new Suggest( );
                suggest.setIdSuggest( daoUtil.getInt( ncpt++ ) );
                suggest.setTitle( daoUtil.getString( ncpt++ ) );
                suggest.setUnavailabilityMessage( daoUtil.getString( ncpt++ ) );
                suggest.setWorkgroup( daoUtil.getString( ncpt++ ) );
                suggest.setIdWorkflow( daoUtil.getInt( ncpt++ ) );

                voteType = new VoteType( );
                voteType.setIdVoteType( daoUtil.getInt( ncpt++ ) );
                suggest.setVoteType( voteType );

                suggest.setNumberVoteRequired( daoUtil.getInt( ncpt++ ) );
                suggest.setNumberDayRequired( daoUtil.getInt( ncpt++ ) );
                suggest.setActiveSuggestSubmitAuthentification( daoUtil.getBoolean( ncpt++ ) );
                suggest.setActiveVoteAuthentification( daoUtil.getBoolean( ncpt++ ) );
                suggest.setActiveCommentAuthentification( daoUtil.getBoolean( ncpt++ ) );
                suggest.setDisableNewSuggestSubmit( daoUtil.getBoolean( ncpt++ ) );
                suggest.setAuthorizedComment( daoUtil.getBoolean( ncpt++ ) );
                suggest.setDisableNewComment( daoUtil.getBoolean( ncpt++ ) );
                suggest.setIdMailingListSuggestSubmit( daoUtil.getInt( ncpt++ ) );
                suggest.setActiveCaptcha( daoUtil.getBoolean( ncpt++ ) );
                suggest.setActive( daoUtil.getBoolean( ncpt++ ) );
                suggest.setDateCreation( daoUtil.getTimestamp( ncpt++ ) );
                suggest.setLibelleValidateButton( daoUtil.getString( ncpt++ ) );
                suggest.setActiveSuggestPropositionState( daoUtil.getBoolean( ncpt++ ) );
                suggest.setLibelleContribution( daoUtil.getString( ncpt++ ) );
                suggest.setNumberSuggestSubmitInTopScore( daoUtil.getInt( ncpt++ ) );
                suggest.setNumberSuggestSubmitInTopComment( daoUtil.getInt( ncpt++ ) );
                suggest.setLimitNumberVote( daoUtil.getBoolean( ncpt++ ) );
                suggest.setNumberSuggestSubmitCaractersShown( daoUtil.getInt( ncpt++ ) );
                suggest.setShowCategoryBlock( daoUtil.getBoolean( ncpt++ ) );
                suggest.setShowTopScoreBlock( daoUtil.getBoolean( ncpt++ ) );
                suggest.setShowTopCommentBlock( daoUtil.getBoolean( ncpt++ ) );
                suggest.setActiveSuggestSubmitPaginator( daoUtil.getBoolean( ncpt++ ) );
                suggest.setNumberSuggestSubmitPerPage( daoUtil.getInt( ncpt++ ) );
                suggest.setRole( daoUtil.getString( ncpt++ ) );
                suggest.setEnableMailNewSuggestSubmit( daoUtil.getBoolean( ncpt++ ) );
                suggest.setHeader( daoUtil.getString( ncpt++ ) );
                suggest.setSortField( daoUtil.getInt( ncpt++ ) );
                suggest.setCodeTheme( daoUtil.getString( ncpt++ ) );
                suggest.setConfirmationMessage( daoUtil.getString( ncpt++ ) );
                suggest.setActiveEditorBbcode( daoUtil.getBoolean( ncpt++ ) );
                suggest.setDefaultSuggest( daoUtil.getBoolean( ncpt++ ) );
                suggest.setIdDefaultSort( daoUtil.getInt( ncpt++ ) );
                suggest.setNotificationNewCommentSenderName( daoUtil.getString( ncpt++ ) );
                suggest.setNotificationNewCommentTitle( daoUtil.getString( ncpt++ ) );
                suggest.setNotificationNewCommentBody( daoUtil.getString( ncpt++ ) );
                suggest.setNotificationNewSuggestSubmitSenderName( daoUtil.getString( ncpt++ ) );
                suggest.setNotificationNewSuggestSubmitTitle( daoUtil.getString( ncpt++ ) );
                suggest.setNotificationNewSuggestSubmitBody( daoUtil.getString( ncpt++ ) );

                suggestList.add( suggest );
            }

            return suggestList;
        }
    }

    /**
     * Modify the order of a suggestsubmit
     * 
     * @param nSortField
     *            The reference field to sort
     * @param nId
     *            The suggest identifier
     * @param plugin
     *            The plugin
     */
    @Override
    public void storeSuggestOrderField( int nId, int nSortField, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_SUGGEST_ORDER, plugin ) )
        {
            daoUtil.setInt( 1, nSortField );
            daoUtil.setInt( 2, nId );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Load all the themes for form xpages
     * 
     * @param plugin
     *            the plugin
     * @return a map containing the themes by form id
     */
    @Override
    public Map<Integer, Theme> getXPageThemesMap( Plugin plugin )
    {
        Map<Integer, Theme> xPageThemesMap = new HashMap<>( );

        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL_THEMES, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                int nIndex = 1;
                int nIdForm = daoUtil.getInt( nIndex++ );
                String strCodeTheme = daoUtil.getString( nIndex++ );
                Theme theme = ThemesService.getGlobalTheme( strCodeTheme );
                xPageThemesMap.put( nIdForm, theme );
            }

            return xPageThemesMap;
        }
    }
}
