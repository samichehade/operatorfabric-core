import {User} from '@ofModel/user.model';
import {Action} from '@ngrx/store';

export enum UserActionsTypes {

    UserApplicationRegistered =         '[User] User application registered',
    UserApplicationNotRegistered =      '[User] User application not registered',
    CreateUserApplication =             '[User] Create the User in the application',
    CreateUserApplicationOnSuccess =    '[User] Create the User in the application on success',
    CreateUserApplicationOnFailure =    '[User] Create the User in the application on failure',
    HandleUnexpectedError =             '[User] Handle unexpected error related to user creation issue'
}

export class UserApplicationRegistered implements Action {
    /* istanbul ignore next */ 
    readonly type = UserActionsTypes.UserApplicationRegistered;
    constructor(public payload : {user : User}) {}
}

export class UserApplicationNotRegistered implements Action {
    /* istanbul ignore next */ 
    readonly type = UserActionsTypes.UserApplicationNotRegistered;
    constructor(public payload : {error : Error, user : User}) {}
}

export class CreateUserApplication implements Action {
    /* istanbul ignore next */ 
    readonly type = UserActionsTypes.CreateUserApplication;
    constructor(public payload : {user : User}) {}
}


export class CreateUserApplicationOnSuccess implements Action {
    /* istanbul ignore next */ 
    readonly type = UserActionsTypes.CreateUserApplicationOnSuccess;
    constructor(public payload : {user : User}) {}
}

export class CreateUserApplicationOnFailure implements Action {
    /* istanbul ignore next */ 
    readonly type = UserActionsTypes.CreateUserApplicationOnFailure;
    constructor(public payload : {error : Error}) {}
}


export class HandleUnexpectedError implements Action {
    /* istanbul ignore next */ 
    readonly type = UserActionsTypes.HandleUnexpectedError;
    constructor(public payload : {error : Error}) {}
}

export type UserActions = UserApplicationRegistered
    | UserApplicationNotRegistered
    | CreateUserApplication
    | CreateUserApplicationOnSuccess
    | CreateUserApplicationOnFailure
    | HandleUnexpectedError;

